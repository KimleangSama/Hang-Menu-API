package com.keakimleang.digital_menu.features.users.entities;

import com.keakimleang.digital_menu.commons.entities.*;
import com.keakimleang.digital_menu.features.users.enums.*;
import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("User")
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"phone"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class User extends BaseEntityAudit implements EntityDeletable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Indexed
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String raw;

    @Column(name = "fullname")
    private String fullname;

    private String email;
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyRelation;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @NotNull(message = "Provider is required")
    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.local;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private AuthStatus status = AuthStatus.pending;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GroupMember> groupMembers = new HashSet<>();

    private LocalDateTime deletedAt;
    private UUID deletedBy;

    @Override
    public UUID getDeletedBy() {
        return deletedBy;
    }

    @Override
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}
