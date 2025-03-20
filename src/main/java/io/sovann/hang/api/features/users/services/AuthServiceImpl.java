package io.sovann.hang.api.features.users.services;


import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.users.entities.Group;
import io.sovann.hang.api.features.users.entities.GroupMember;
import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthProvider;
import io.sovann.hang.api.features.users.enums.AuthRole;
import io.sovann.hang.api.features.users.payloads.request.LoginRequest;
import io.sovann.hang.api.features.users.payloads.request.RegisterRequest;
import io.sovann.hang.api.features.users.payloads.response.AuthResponse;
import io.sovann.hang.api.features.users.repos.GroupMemberRepository;
import io.sovann.hang.api.features.users.repos.GroupRepository;
import io.sovann.hang.api.features.users.repos.UserRepository;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.RandomString;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import io.sovann.hang.api.utils.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl {
    private final UserRepository userRepository;
    private final RoleServiceImpl roleService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;

    @Transactional
    public User register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getUsername());
        user.setFullname(request.getFullname());
        user.setRaw(request.getPassword());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        setRoles(user, request.getRoles());
        user.setProvider(AuthProvider.local);
        User savedUser = userRepository.save(user);
        if (!request.isGroupMember()) {
            Group group = new Group();
            group.setName(request.getUsername() + "_" + RandomString.make(6));
            group.setDescription("Group for user " + request.getUsername());
            group.setCreatedBy(savedUser.getId());
            groupRepository.save(group);
            GroupMember groupMember = new GroupMember();
            groupMember.setGroup(group);
            groupMember.setUser(user);
            groupMemberRepository.save(groupMember);
        }
        return savedUser;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + request.getUsername() + " not found."));
        Authentication authentication = authenticate(request.getUsername(), request.getPassword());
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedBy(user.getId());
        userRepository.save(user);
        String accessToken = tokenProvider.generateAccessToken(customUserDetails);
        String refreshToken = tokenProvider.generateRefreshToken(customUserDetails);
        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                tokenProvider.getExpirationDateFromToken(accessToken)
        );
    }

    private void setRoles(User user, List<AuthRole> roles) {
        if (roles.isEmpty()) {
            Role userRole = roleService.findByName(AuthRole.guest);
            user.getRoles().add(userRole);
        } else {
            try {
                List<Role> foundRoles = roleService.findByNames(roles);
                user.getRoles().addAll(foundRoles);
            } catch (ResourceNotFoundException e) {
                log.error("Roles not found: {} with message: {}", roles, e.getMessage());
            }
        }
    }

    private Authentication authenticate(String username, String password) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Username or password are incorrect.");
        }
    }

    public AuthResponse refreshToken(HttpServletRequest request) {
        String refreshToken = extractRefreshToken(request);
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username: " + username));
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        if (tokenProvider.validateToken(refreshToken)) {
            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            var accessToken = tokenProvider.generateAccessToken(customUserDetails);
            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    user.getUsername(),
                    tokenProvider.getExpirationDateFromToken(accessToken)
            );
        } else {
            throw new BadCredentialsException("Refresh token is invalid.");
        }
    }

    private String extractRefreshToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadCredentialsException("Refresh token is required.");
        }
        return authHeader.substring(7);
    }
}