package io.sovann.hang.api.features.orders.entities;

import io.sovann.hang.api.features.orders.enums.*;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.users.entities.*;
import jakarta.persistence.*;
import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Order")
@Getter
@Setter
@ToString
@Entity
@Table(name = "orders")
public class Order extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    private Double totalAmountInRiel;
    private Double totalAmountInDollar;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.pending;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    private String specialInstructions;

    private String phoneNumber;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderMenu> orderMenus;
}