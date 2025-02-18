package io.sovann.hang.api.features.orders.payloads.requests;

import io.sovann.hang.api.features.orders.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CreateOrderRequest {
    private UUID cartId;
    private UUID tableId;
    private double totalAmount;
    private OrderStatus status;
    private LocalDateTime orderTime;
    private String specialInstructions;
}
