package io.sovann.hang.api.features.carts.entities;

import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.users.entities.BaseEntityAudit;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;

@RedisHash("CartMenu")
@Getter
@Setter
@ToString
@Entity
@Table(name = "cart_menus")
public class CartMenu extends BaseEntityAudit {
    @Serial
    private final static long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private int quantity;
    private String specialRequests;
}
