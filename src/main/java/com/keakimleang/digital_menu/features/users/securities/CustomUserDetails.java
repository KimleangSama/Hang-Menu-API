package com.keakimleang.digital_menu.features.users.securities;


import com.keakimleang.digital_menu.features.users.entities.User;
import com.keakimleang.digital_menu.features.users.enums.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;

public class CustomUserDetails implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = 5630699925975073133L;

    @Getter
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        user.getRoles().forEach(role ->
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().name())));
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        String status = user.getStatus().name();
        return status.equalsIgnoreCase(AuthStatus.active.name())
                || status.equalsIgnoreCase(AuthStatus.pending.name());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        String status = user.getStatus().name();
        return status.equalsIgnoreCase(AuthStatus.active.name())
                || status.equalsIgnoreCase(AuthStatus.pending.name());
    }
}
