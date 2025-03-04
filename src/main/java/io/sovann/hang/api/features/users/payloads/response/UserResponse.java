package io.sovann.hang.api.features.users.payloads.response;

import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthProvider;
import io.sovann.hang.api.utils.SoftEntityDeletable;
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

    public static UserResponse fromUser(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setFullname(user.getFullname());
        userResponse.setEmail(user.getEmail());
        userResponse.setProfileUrl(user.getProfileUrl());
        userResponse.setProvider(user.getProvider());
        userResponse.setRoles(RoleResponse.fromRoles(user.getRoles()));
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getUpdatedAt());
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
                })
                .map(UserResponse::fromUser)
                .toList();
    }

    public static UserResponse fromEntity(User user) {
        return fromUser(user);
    }
}
