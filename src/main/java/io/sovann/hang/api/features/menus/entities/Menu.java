package io.sovann.hang.api.features.menus.entities;

import io.sovann.hang.api.features.users.entities.BaseEntityAudit;
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
@Table(name = "menus")
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
}
