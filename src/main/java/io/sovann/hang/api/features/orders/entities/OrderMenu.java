package io.sovann.hang.api.features.orders.entities;

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
    private Double unitPrice;
    private String specialRequests;

    public OrderMenu() {}

    public OrderMenu(Menu menu, Integer quantity, Double unitPrice, String specialRequests) {
        this.menu = menu;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.specialRequests = specialRequests;
    }
}