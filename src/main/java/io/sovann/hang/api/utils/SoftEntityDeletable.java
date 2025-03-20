package io.sovann.hang.api.utils;

import io.sovann.hang.api.exceptions.ResourceDeletedException;
import io.sovann.hang.api.exceptions.ResourceForbiddenException;
import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.commons.entities.EntityDeletable;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthStatus;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;

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

    public static void throwErrorIfSoftDeleted(CustomUserDetails user) {
        if (user == null) {
            throw new ResourceNotFoundException("User", "id: " + "unknown");
        }
        throwErrorIfSoftDeleted(user.getUser());
    }

    public static void throwErrorIfSoftDeleted(User user) {
        throwErrorIfSoftDeleted(user, User.class);
        // Ensure user status is active
        if (user.getStatus() == AuthStatus.blocked || user.getStatus() == AuthStatus.deleted) {
            throw new ResourceForbiddenException(user.getUsername(), User.class);
        }
    }
}
