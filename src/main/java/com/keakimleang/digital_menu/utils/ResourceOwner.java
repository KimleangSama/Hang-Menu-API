package com.keakimleang.digital_menu.utils;

import com.keakimleang.digital_menu.features.menus.entities.Category;
import com.keakimleang.digital_menu.features.menus.entities.Menu;
import com.keakimleang.digital_menu.features.stores.entities.Store;
import com.keakimleang.digital_menu.features.users.entities.Group;
import com.keakimleang.digital_menu.features.users.entities.GroupMember;
import com.keakimleang.digital_menu.features.users.entities.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class ResourceOwner {
    public static boolean hasPermission(User user, Store store) {
        return user != null && Optional.ofNullable(store)
                .map(Store::getGroup)
                .map(Group::getMembers)
                .map(members -> members.stream()
                        .anyMatch(member -> isUserMatch(member, user)))
                .orElse(false);
    }

    public static boolean hasPermission(User user, Group group) {
        return user != null && Optional.ofNullable(group)
                .map(Group::getMembers)
                .map(members -> members.stream()
                        .anyMatch(member -> isUserMatch(member, user)))
                .orElse(false);
    }

    public static boolean hasPermission(User user, Category category) {
        return user != null && Optional.ofNullable(category)
                .map(Category::getGroup)
                .map(Group::getMembers)
                .map(members -> members.stream()
                        .anyMatch(member -> isUserMatch(member, user)))
                .orElse(false);
    }

    public static boolean hasPermission(User user, Menu menu) {
        return user != null && Optional.ofNullable(menu)
                .map(Menu::getGroup)
                .map(Group::getMembers)
                .map(members -> members.stream()
                        .anyMatch(member -> isUserMatch(member, user)))
                .orElse(false);
    }

    private static boolean isUserMatch(GroupMember member, User user) {
        return Optional.ofNullable(member)
                .map(GroupMember::getUser)
                .map(User::getId)
                .map(id -> id.equals(user.getId()))
                .orElse(false);
    }

    public static boolean isAdmin(User user) {
        return user != null && user.getRoles().stream()
                .anyMatch(role -> role.getName().name().equalsIgnoreCase("admin"));
    }
}
