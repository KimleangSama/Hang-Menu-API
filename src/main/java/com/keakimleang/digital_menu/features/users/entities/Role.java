package com.keakimleang.digital_menu.features.users.entities;


import com.keakimleang.digital_menu.features.users.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"}, name = "unq_name")
})
public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthRole name;

    public Role() {
    }

    public Role(AuthRole name) {
        this.name = name;
    }
}

