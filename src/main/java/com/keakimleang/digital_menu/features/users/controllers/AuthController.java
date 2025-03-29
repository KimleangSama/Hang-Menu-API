package com.keakimleang.digital_menu.features.users.controllers;

import com.keakimleang.digital_menu.annotations.CurrentUser;
import com.keakimleang.digital_menu.commons.payloads.BaseResponse;
import com.keakimleang.digital_menu.constants.APIURLs;
import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.payloads.request.LoginRequest;
import com.keakimleang.digital_menu.features.users.payloads.request.RegisterRequest;
import com.keakimleang.digital_menu.features.users.payloads.response.AuthResponse;
import com.keakimleang.digital_menu.features.users.payloads.response.UserResponse;
import com.keakimleang.digital_menu.features.users.securities.CustomUserDetails;
import com.keakimleang.digital_menu.features.users.services.AuthServiceImpl;
import com.keakimleang.digital_menu.features.users.services.UserServiceImpl;
import com.keakimleang.digital_menu.utils.SoftEntityDeletable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "me", key = "#user.username")
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