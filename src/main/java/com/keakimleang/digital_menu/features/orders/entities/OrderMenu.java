package com.keakimleang.digital_menu.features.orders.entities;

import com.keakimleang.digital_menu.commons.entities.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
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