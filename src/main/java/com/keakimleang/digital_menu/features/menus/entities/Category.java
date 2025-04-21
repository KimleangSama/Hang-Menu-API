package com.keakimleang.digital_menu.features.menus.entities;

import com.keakimleang.digital_menu.commons.entities.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.users.entities.*;
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
        @UniqueConstraint(columnNames = {"name", "store_id"})
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
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Menu> menus;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
}
