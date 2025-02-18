package io.sovann.hang.api.features.users.services;


import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.repos.UserRepository;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserRepository userRepository;

    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        User found = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " not found."));
        SoftEntityDeletable.throwErrorIfSoftDeleted(found);
        return found;
    }

    @Cacheable(value = "users", key = "#email")
    public User findByEmail(String email) {
        User found = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found."));
        SoftEntityDeletable.throwErrorIfSoftDeleted(found);
        return found;
    }
}