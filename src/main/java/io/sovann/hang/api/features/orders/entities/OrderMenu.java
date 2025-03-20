package io.sovann.hang.api.features.orders.entities;

import io.sovann.hang.api.commons.entities.BaseEntityAudit;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.util.UUID;

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

    private UUID menuId;
    private String code;
    private String name;
    private String image;
    private String description;

    private double price;
    private double discount;
    private String currency;
    private int quantity;
    private String specialRequests;

    public OrderMenu() {
    }
}