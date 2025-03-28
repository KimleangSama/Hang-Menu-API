package com.keakimleang.digital_menu.features.menus.entities;

import com.keakimleang.digital_menu.commons.entities.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import jakarta.persistence.*;
import java.io.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Favorite")
@Getter
@Setter
@ToString
@Entity
@Table(name = "favorites", uniqueConstraints = {
        @jakarta.persistence.UniqueConstraint(columnNames = {"user_id", "menu_id"})
})
public class Favorite extends BaseEntityAudit {
    @Serial
    private final static long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;
}
