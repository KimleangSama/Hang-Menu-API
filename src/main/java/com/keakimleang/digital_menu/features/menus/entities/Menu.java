package com.keakimleang.digital_menu.features.menus.entities;

import com.keakimleang.digital_menu.commons.entities.BaseEntityAudit;
import com.keakimleang.digital_menu.features.users.entities.Group;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.util.List;

@RedisHash("Menu")
@Getter
@Setter
@ToString
@Entity
@Table(name = "menus", indexes = {
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

    private Boolean isAvailable = true;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ToString.Exclude
    @OneToMany(mappedBy = "menu")
    private List<MenuImage> images;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "menu_badges", joinColumns = @JoinColumn(name = "menu_id"))
    private List<String> badges;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;
}
