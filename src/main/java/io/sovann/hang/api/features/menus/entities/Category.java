package io.sovann.hang.api.features.menus.entities;

import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.users.entities.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Category")
@Getter
@Setter
@ToString
@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "store_id"}),
        @UniqueConstraint(columnNames = {"position", "store_id"})
})
public class Category extends BaseEntityAudit {
    @Serial
    private final static long serialVersionUID = 1L;

    @Column(nullable = false)
    private String name;
    private String description;
    private String icon;
    private boolean isHidden = false;
    private boolean isAvailable = true;

    private int position = 0;

    @ToString.Exclude
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<Menu> menus;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
