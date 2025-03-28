package com.keakimleang.digital_menu.features.users.payloads.request;

import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.enums.*;
import jakarta.validation.constraints.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class RegisterToGroupRequest {
    private UUID groupId;
    @NotBlank
    private String username;
    private String fullname;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyRelation;
    private String profileUrl;
    private AuthStatus status = AuthStatus.pending;
    private List<UUID> roles;

    public static User fromRequest(RegisterToGroupRequest request, Set<Role> roles) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setFullname(request.getFullname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEmergencyContact(request.getEmergencyContact());
        user.setEmergencyRelation(request.getEmergencyRelation());
        user.setProfileUrl(request.getProfileUrl());
        user.setStatus(request.getStatus());
        user.setRoles(roles);
        return user;
    }
}