package io.sovann.hang.api.features.users.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.commons.payloads.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.payloads.request.*;
import io.sovann.hang.api.features.users.payloads.response.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.features.users.services.*;
import io.sovann.hang.api.utils.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.security.access.prepost.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(APIURLs.AUTH)
public class AuthController {
    private final UserServiceImpl userService;
    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public BaseResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return BaseResponse.<AuthResponse>ok().setPayload(response);
        } catch (UsernameNotFoundException e) {
            return BaseResponse.<AuthResponse>notFound()
                    .setError(e.getMessage());
        } catch (BadCredentialsException e) {
            return BaseResponse.<AuthResponse>wrongCredentials()
                    .setError(e.getMessage());
        } catch (Exception e) {
            return BaseResponse.<AuthResponse>exception()
                    .setError(e.getMessage());
        }
    }

    @PostMapping("/register")
    public BaseResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            try {
                User user = userService.findByUsername(request.getUsername());
                if (user != null) {
                    return BaseResponse.<UserResponse>duplicateEntity()
                            .setError("User with " + "username" + " " + request.getUsername() + " already exists.");
                } else {
                    throw new UsernameNotFoundException("User not found.");
                }
            } catch (UsernameNotFoundException e) {
                User user = authService.register(request);
                return BaseResponse.<UserResponse>created().setPayload(UserResponse.fromEntity(user));
            }
        } catch (Exception e) {
            log.error("User registration failed. Reason: {}", e.getMessage(), e);
            return BaseResponse.<UserResponse>badRequest().setError("User registration failed. Reason: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    @PreAuthorize("authenticated")
    public BaseResponse<UserResponse> me(@CurrentUser CustomUserDetails user) {
        try {
            SoftEntityDeletable.throwErrorIfSoftDeleted(user);
            return BaseResponse.<UserResponse>ok().setPayload(UserResponse.fromEntity(user.getUser()));
        } catch (Exception e) {
            return BaseResponse.<UserResponse>exception().setError("Failed to get current user. Reason: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public BaseResponse<AuthResponse> refreshToken(HttpServletRequest request) {
        try {
            AuthResponse res = authService.refreshToken(request);
            return BaseResponse.<AuthResponse>ok().setPayload(res);
        } catch (Exception e) {
            return BaseResponse.<AuthResponse>exception().setError("Failed to refresh token. Reason: " + e.getMessage());
        }
    }
}