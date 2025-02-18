package io.sovann.hang.api.features.users.payloads.response;

import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.enums.AuthRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class RoleResponse implements Serializable {
    private UUID id;
    private String name;

    private RoleResponse(Role role) {
        this.id = role.getId();
        this.name = role.getName().name();
    }

    public static Set<RoleResponse> fromRoles(Set<Role> roles) {
        return roles.stream().map(RoleResponse::new).collect(Collectors.toSet());
    }

    public static List<RoleResponse> fromEntities(List<Role> roles) {
        return roles.stream().map(RoleResponse::new).collect(Collectors.toList());
    }

    public static List<RoleResponse> fromEntitiesExclude(List<Role> roles, AuthRole authRole) {
        return roles.stream()
                .filter(role -> !role.getName().equals(authRole))
                .map(RoleResponse::new)
                .collect(Collectors.toList());
    }
}