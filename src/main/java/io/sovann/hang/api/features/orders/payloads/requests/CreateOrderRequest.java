package io.sovann.hang.api.features.orders.payloads.requests;

import io.sovann.hang.api.features.orders.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CreateOrderRequest {
    private UUID code;
    private UUID storeId;
    private LocalDateTime orderTime;
    private OrderStatus status;
    private String phoneNumber;
    private String specialInstructions;
    private List<CreateOrderMenuRequest> orderMenus;
}
