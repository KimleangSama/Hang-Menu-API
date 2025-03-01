package io.sovann.hang.api.features.menus.entities;

import io.sovann.hang.api.features.users.entities.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Menu")
@Getter
@Setter
@ToString
@Entity
@Table(name = "menus", indexes = {
        @Index(name = "idx_menu_code", columnList = "code"),
        @Index(name = "idx_menu_name", columnList = "name"),
})
public class Menu extends BaseEntityAudit {
    @Serial
    private final static long serialVersionUID = 1L;

    private String code;
    private String name;
    private String description;

    private Double price;
    private Double discount;
    private String currency;

    private String image;
    private Boolean isHidden = false;
    private Boolean isAvailable = true;
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "menu")
    private List<MenuImage> images;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "menu_badges", joinColumns = @JoinColumn(name = "menu_id"))
    private List<String> badges;
}
