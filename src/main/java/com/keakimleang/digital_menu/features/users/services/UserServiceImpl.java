package com.keakimleang.digital_menu.features.users.services;


import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.repos.*;
import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.repos.*;
import com.keakimleang.digital_menu.utils.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        User found = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found."));
        SoftEntityDeletable.throwErrorIfSoftDeleted(found);
        return found;
    }

    @Transactional
    public Store findStoreByGroup(Group group) {
        return storeRepository.findByGroupId(group.getId())
                .orElseThrow(() -> new UsernameNotFoundException("Store of group: " + group.getId() + " not found."));
    }
}