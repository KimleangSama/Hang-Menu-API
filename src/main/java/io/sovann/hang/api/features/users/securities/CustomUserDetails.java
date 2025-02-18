package io.sovann.hang.api.features.users.securities;


import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
