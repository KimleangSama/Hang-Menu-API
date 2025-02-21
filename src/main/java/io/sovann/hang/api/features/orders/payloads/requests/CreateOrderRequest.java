package io.sovann.hang.api.features.orders.payloads.requests;

import io.sovann.hang.api.features.orders.enums.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateOrderRequest {
    private UUID storeId;
    private LocalDateTime orderTime;
    private OrderStatus status;
    private String phoneNumber;
    private String specialInstructions;
    private List<CreateOrderMenuRequest> orderMenus;
}
