package com.keakimleang.digital_menu.features.users.payloads.response;

import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.enums.AuthProvider;
import com.keakimleang.digital_menu.utils.SoftEntityDeletable;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse implements Serializable {
    private UUID id;
    private String username;
    private String fullname;
    private String email;
    private String profileUrl;
    private AuthProvider provider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<RoleResponse> roles;

    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setFullname(user.getFullname());
        response.setEmail(user.getEmail());
        response.setProfileUrl(user.getProfileUrl());
        response.setProvider(user.getProvider());
        response.setRoles(RoleResponse.fromRoles(user.getRoles()));
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        if (user.getRoles() != null) {
            response.setRoles(RoleResponse.fromRoles(user.getRoles()));
        }
        return response;
    }

    public static List<UserResponse> fromEntities(List<User> users) {
        return users.stream()
                .filter(user -> {
                    try {
                        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
                        return true;
                    } catch (Exception e) {
                        log.debug(e.getMessage());
                        return false;
                    }
                }).map(UserResponse::fromEntity)
                .toList();
    }
}
