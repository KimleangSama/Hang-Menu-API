package com.keakimleang.digital_menu.features.users.services;


import com.keakimleang.digital_menu.constants.CacheValue;
import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.repos.UserRepository;
import com.keakimleang.digital_menu.features.users.securities.CustomUserDetails;
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

    @Cacheable(value = CacheValue.USER_DETAILS, key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username)));
        User found = user.orElse(null);
        return new CustomUserDetails(found);
    }
}

