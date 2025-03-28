package com.keakimleang.digital_menu.features.users.payloads.response;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.enums.*;
import com.keakimleang.digital_menu.utils.*;
import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;

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
        UserResponse userResponse = new UserResponse();
        MMConfig.mapper().map(user, userResponse);
        if (user.getRoles() != null) {
            userResponse.setRoles(RoleResponse.fromRoles(user.getRoles()));
        }
        return userResponse;
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
