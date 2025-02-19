package io.sovann.hang.api.features.orders.entities;

import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.users.entities.*;
import jakarta.persistence.*;
import java.io.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("OrderMenu")
@Getter
@Setter
@ToString
@Entity
@Table(name = "order_menus")
public class OrderMenu extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;

    private int quantity;
    private String specialRequests;

    public OrderMenu() {}

    public OrderMenu(Menu menu, Integer quantity, String specialRequests) {
        this.menu = menu;
        this.quantity = quantity;
        this.specialRequests = specialRequests;
    }
}