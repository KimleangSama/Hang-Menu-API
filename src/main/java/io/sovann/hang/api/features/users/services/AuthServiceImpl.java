package io.sovann.hang.api.features.users.services;


import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.users.entities.Group;
import io.sovann.hang.api.features.users.entities.GroupMember;
import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthProvider;
import io.sovann.hang.api.features.users.enums.AuthRole;
import io.sovann.hang.api.features.users.payloads.request.LoginBackOfficeRequest;
import io.sovann.hang.api.features.users.payloads.request.LoginFrontOfficeRequest;
import io.sovann.hang.api.features.users.payloads.request.RegisterBackOfficeRequest;
import io.sovann.hang.api.features.users.payloads.request.RegisterFrontOfficeRequest;
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
    public User registerUser(RegisterBackOfficeRequest request) {
        return registerUser(request.getUsername(), RandomString.make(8) + "@mail.com", request.getPassword(), request.getRoles());
    }

    @Transactional
    public User registerUser(RegisterFrontOfficeRequest request) {
        return registerUser(request.getEmail(), request.getEmail(), request.getPassword(), request.getRoles());
    }

    private User registerUser(String username, String email, String password, List<AuthRole> roles) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRaw(password);
        user.setPassword(passwordEncoder.encode(password));
        setRoles(user, roles);
        user.setProvider(AuthProvider.local);

        User savedUser = userRepository.save(user);

        Group group = new Group();
        group.setName(username + "_" + RandomString.make(6));
        group.setDescription("Group for user " + username);
        group.setCreatedBy(savedUser.getId());
        groupRepository.save(group);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMemberRepository.save(groupMember);

        return savedUser;
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

    @Transactional
    public AuthResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username " + username + " not found."));
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        Authentication authentication = authenticate(username, password);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        updateUserLoginDetails(user);
        String accessToken = tokenProvider.generateAccessToken(customUserDetails);
        String refreshToken = tokenProvider.generateRefreshToken(customUserDetails);
        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                tokenProvider.getExpirationDateFromToken(accessToken)
        );
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

    private void updateUserLoginDetails(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedBy(user.getId());
        userRepository.save(user);
    }

    @Transactional
    public AuthResponse loginBackOfficeUser(LoginBackOfficeRequest request) {
        return loginUser(request.getUsername(), request.getPassword());
    }

    @Transactional
    public AuthResponse loginFrontOfficeUser(LoginFrontOfficeRequest request) {
        return loginUser(request.getEmail(), request.getPassword());
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