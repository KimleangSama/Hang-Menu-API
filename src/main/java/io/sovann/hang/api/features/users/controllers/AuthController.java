package io.sovann.hang.api.features.users.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthProvider;
import io.sovann.hang.api.features.users.payloads.request.LoginBackOfficeRequest;
import io.sovann.hang.api.features.users.payloads.request.LoginFrontOfficeRequest;
import io.sovann.hang.api.features.users.payloads.request.RegisterBackOfficeRequest;
import io.sovann.hang.api.features.users.payloads.request.RegisterFrontOfficeRequest;
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

    @PostMapping("/login-backoffice")
    public BaseResponse<AuthResponse> loginBackOfficeUser(@Valid @RequestBody LoginBackOfficeRequest request) {
        return handleLogin(() -> authService.loginBackOfficeUser(request), "username", request.getUsername());
    }

    @PostMapping("/login-frontoffice")
    public BaseResponse<AuthResponse> loginFrontOfficeUser(@Valid @RequestBody LoginFrontOfficeRequest request) {
        return handleLogin(() -> authService.loginFrontOfficeUser(request), "email", request.getEmail());
    }

    private BaseResponse<AuthResponse> handleLogin(AuthenticatedOperation operation, String identifierType, String identifier) {
        try {
            AuthResponse response = operation.authenticate();
            return BaseResponse.<AuthResponse>ok().setPayload(response);
        } catch (UsernameNotFoundException e) {
            return createErrorResponse("User with " + identifierType + " " + identifier + " not found.");
        } catch (BadCredentialsException e) {
            return createErrorResponse(e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("User authentication failed. " + e.getMessage());
        }
    }

    @PostMapping("/register-backoffice")
    public BaseResponse<UserResponse> registerBackOfficeUser(@Valid @RequestBody RegisterBackOfficeRequest request) {
        return handleUserRegistration(() -> authService.registerUser(request), request.getUsername(), "username");
    }

    @PostMapping("/register-frontoffice")
    public BaseResponse<UserResponse> registerFrontOfficeUser(@Valid @RequestBody RegisterFrontOfficeRequest request) {
        return handleUserRegistration(() -> authService.registerUser(request), request.getEmail(), "email");
    }

    private BaseResponse<UserResponse> handleUserRegistration(UserRegistrationOperation operation, String identifier, String identifierType) {
        try {
            try {
                User user;
                if (identifierType.equalsIgnoreCase(identifier)) {
                    user = userService.findByEmail(identifier);
                } else {
                    user = userService.findByUsername(identifier);
                }
                if (user != null) {
                    return createUserAlreadyExistsResponse(user, identifierType, identifier);
                }
                user = operation.register();
                return BaseResponse.<UserResponse>created().setPayload(UserResponse.fromUser(user));
            } catch (UsernameNotFoundException e) {
                User user = operation.register();
                return BaseResponse.<UserResponse>created().setPayload(UserResponse.fromUser(user));
            }
        } catch (Exception e) {
            log.error("User registration failed. Reason: {}", e.getMessage(), e);
            return BaseResponse.<UserResponse>badRequest().setError("User registration failed. Reason: " + e.getMessage());
        }
    }

    private BaseResponse<UserResponse> createUserAlreadyExistsResponse(User user, String identifierType, String identifier) {
        String message = "User with " + identifierType + " " + identifier + " already exists.";
        if (!user.getProvider().equals(AuthProvider.local)) {
            message = "Looks like you're already registered with " + user.getProvider() + " account. Please login with your " + user.getProvider() + " account.";
        }
        return BaseResponse.<UserResponse>badRequest().setError(message);
    }

    @GetMapping("/me")
    @PreAuthorize("authenticated")
    public BaseResponse<UserResponse> getCurrentUser(
            @CurrentUser CustomUserDetails user
    ) {
        try {
            SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
            log.info("Getting current user: {}", user.getUser());
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