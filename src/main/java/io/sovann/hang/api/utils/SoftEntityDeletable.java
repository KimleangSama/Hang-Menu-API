package io.sovann.hang.api.utils;

import io.sovann.hang.api.exceptions.ResourceDeletedException;
import io.sovann.hang.api.exceptions.ResourceForbiddenException;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.commons.entities.EntityDeletable;
import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthRole;
import io.sovann.hang.api.features.users.enums.AuthStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class SoftEntityDeletable {
    // Validate entity deletion state
    public static <T> void throwErrorIfSoftDeleted(T entity, Class<T> entityClass) {
        if (entity == null) {
            throw new ResourceNotFoundException(entityClass.getName(), "id: " + "unknown");
        }

        if (entity instanceof EntityDeletable deletableEntity) {
            // Check if already deleted
            if (deletableEntity.getDeletedAt() != null || deletableEntity.getDeletedBy() != null) {
                throw new ResourceDeletedException(entityClass, deletableEntity.getDeletedAt(), deletableEntity.getDeletedBy());
            }
        }
    }

    public static void throwErrorIfSoftDeleted(User user) {
        throwErrorIfSoftDeleted(user, User.class);
        // Ensure user status is active
        if (user.getStatus() == AuthStatus.blocked || user.getStatus() == AuthStatus.deleted) {
            throw new ResourceForbiddenException(user.getUsername(), User.class);
        }
    }

    // Validate roles and handle invalid roles in one place
    private static void checkUserRoles(Collection<Role> roles) {
        Set<String> validRoleNames = EnumSet.allOf(AuthRole.class).stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        roles.forEach(role -> {
            throwErrorIfSoftDeleted(role, Role.class);
            if (!validRoleNames.contains(role.getName().name())) {
                throw new ResourceNotFoundException("Role", "name");
            }
        });
    }
}
