package com.keakimleang.digital_menu.utils;

import com.keakimleang.digital_menu.features.menus.entities.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import lombok.extern.slf4j.*;

@Slf4j
public class ResourceOwner {
    public static boolean hasPermission(User user, Store store) {
        return user != null && store.getGroup().getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));
    }

    public static boolean hasPermission(User user, Category category) {
        try {
            return user != null && category.getGroup().getMembers().stream()
                    .anyMatch(member -> member.getUser().getId().equals(user.getId()));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            return false;
        }
    }

    public static boolean hasPermission(User user, Menu menu) {
        try {
            return user != null && menu.getGroup().getMembers().stream()
                    .anyMatch(member -> member.getUser().getId().equals(user.getId()));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            return false;
        }
    }

    public static boolean isAdmin(User user) {
        return user != null && user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equalsIgnoreCase("admin"));
    }
}
