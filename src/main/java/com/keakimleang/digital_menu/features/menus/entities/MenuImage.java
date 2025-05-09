package com.keakimleang.digital_menu.features.menus.entities;

import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("MenuImage")
@Getter
@Setter
@ToString
@Entity
@Table(name = "menu_images", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"url"})
})
public class MenuImage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Indexed
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String name;
    private String url;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    public MenuImage() {
    }

    public MenuImage(Menu menu, String filename, String url) {
        this.menu = menu;
        this.name = filename;
        this.url = url;
    }
}
