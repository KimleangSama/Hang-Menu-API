package io.sovann.hang.api.features.users.payloads.response;

import io.sovann.hang.api.configs.*;
import io.sovann.hang.api.features.users.entities.*;
import io.sovann.hang.api.features.users.enums.*;
import io.sovann.hang.api.utils.*;
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
