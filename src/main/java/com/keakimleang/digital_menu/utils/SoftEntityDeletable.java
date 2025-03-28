package com.keakimleang.digital_menu.utils;

import com.keakimleang.digital_menu.commons.entities.*;
import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.enums.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import lombok.extern.slf4j.*;

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
