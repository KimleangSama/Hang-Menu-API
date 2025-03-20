package io.sovann.hang.api.utils;

import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.users.entities.User;
import lombok.extern.slf4j.Slf4j;

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
}
