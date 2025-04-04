package com.keakimleang.digital_menu.features.users.services;


import com.keakimleang.digital_menu.constants.CacheValue;
import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.repos.UserRepository;
import com.keakimleang.digital_menu.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserRepository userRepository;

    @Cacheable(value = CacheValue.USERS, key = "#username")
    public User findByUsername(String username) {
        User found = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found."));
        SoftEntityDeletable.throwErrorIfSoftDeleted(found);
        return found;
    }

    @Cacheable(value = CacheValue.USERS, key = "#id")
    public User findById(UUID id) {
        User found = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id: " + id + " not found."));
        SoftEntityDeletable.throwErrorIfSoftDeleted(found);
        return found;
    }

    @CacheEvict(value = CacheValue.USERS, allEntries = true)
    public void deleteUser(UUID deleter, UUID id) {
        User found = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with id: " + id + " not found."));
        SoftEntityDeletable.throwErrorIfSoftDeleted(found);
        found.setDeletedAt(LocalDateTime.now());
        found.setDeletedBy(deleter);
        userRepository.save(found);
    }
}