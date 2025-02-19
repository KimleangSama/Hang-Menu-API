package io.sovann.hang.api.features.orders.payloads.requests;

import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateOrderRequest {
    private double totalAmount;
    private LocalDateTime orderTime;
    private String phoneNumber;
    private String specialInstructions;
    private List<CreateOrderMenuRequest> orderMenus;
}
