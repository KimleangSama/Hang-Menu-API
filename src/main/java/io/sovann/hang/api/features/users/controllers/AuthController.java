package io.sovann.hang.api.features.users.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.payloads.request.LoginRequest;
import io.sovann.hang.api.features.users.payloads.request.RegisterRequest;
import io.sovann.hang.api.features.users.payloads.response.AuthResponse;
import io.sovann.hang.api.features.users.payloads.response.UserResponse;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.features.users.services.AuthServiceImpl;
import io.sovann.hang.api.features.users.services.UserServiceImpl;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        return handleLogin(() -> authService.login(request), request.getUsername());
    }

    private BaseResponse<AuthResponse> handleLogin(AuthenticatedOperation operation, String identifier) {
        try {
            AuthResponse response = operation.authenticate();
            return BaseResponse.<AuthResponse>ok().setPayload(response);
        } catch (UsernameNotFoundException e) {
            return createErrorResponse("User with " + "username" + " " + identifier + " not found.");
        } catch (BadCredentialsException e) {
            return createErrorResponse(e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("User authentication failed. " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public BaseResponse<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return handleUserRegistration(() -> authService.register(request), request.getUsername());
    }

    private BaseResponse<UserResponse> handleUserRegistration(UserRegistrationOperation operation, String identifier) {
        try {
            try {
                User user = userService.findByUsername(identifier);
                if (user != null) {
                    return BaseResponse.<UserResponse>duplicateEntity()
                            .setError("User with " + "username" + " " + identifier + " already exists.");
                } else {
                    throw new UsernameNotFoundException("User not found.");
                }
            } catch (UsernameNotFoundException e) {
                User user = operation.register();
                return BaseResponse.<UserResponse>created().setPayload(UserResponse.fromUser(user));
            }
        } catch (Exception e) {
            log.error("User registration failed. Reason: {}", e.getMessage(), e);
            return BaseResponse.<UserResponse>badRequest().setError("User registration failed. Reason: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    @PreAuthorize("authenticated")
    public BaseResponse<UserResponse> getCurrentUser(
            @CurrentUser CustomUserDetails user
    ) {
        try {
            SoftEntityDeletable.throwErrorIfSoftDeleted(user);
            return BaseResponse.<UserResponse>ok().setPayload(UserResponse.fromUser(user.getUser()));
        } catch (Exception e) {
            return BaseResponse.<UserResponse>exception().setError("Failed to get current user. Reason: " + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public BaseResponse<AuthResponse> refreshToken(HttpServletRequest request) {
        try {
            AuthResponse authResponse = authService.refreshToken(request);
            return BaseResponse.<AuthResponse>ok().setPayload(authResponse);
        } catch (Exception e) {
            return createErrorResponse("Failed to refresh token. Reason: " + e.getMessage());
        }
    }

    private BaseResponse<AuthResponse> createErrorResponse(String message) {
        return BaseResponse.<AuthResponse>exception().setError(message).setPayload(null);
    }


    private interface AuthenticatedOperation {
        AuthResponse authenticate() throws Exception;
    }

    private interface UserRegistrationOperation {
        User register() throws Exception;
    }
}