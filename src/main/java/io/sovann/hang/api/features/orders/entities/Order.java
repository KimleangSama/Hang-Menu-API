package io.sovann.hang.api.features.orders.entities;

import io.sovann.hang.api.features.orders.enums.OrderStatus;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.users.entities.BaseEntityAudit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("Order")
@Getter
@Setter
@ToString
@Entity
@Table(name = "orders")
public class Order extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.pending;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "order_time")
    private LocalDateTime orderTime;

    private String specialInstructions;

    @OneToMany(mappedBy = "order")
    private List<OrderMenu> orderMenus;
}