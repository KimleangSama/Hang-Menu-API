package io.sovann.hang.api.features.users.services;


import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.repos.UserRepository;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Cacheable(value = "userDetails", key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username)));
        User found = user.orElse(null);
        return new CustomUserDetails(found);
    }
}

