package com.keakimleang.digital_menu.features.orders.payloads.requests;

import com.keakimleang.digital_menu.features.orders.enums.*;
import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateOrderRequest implements Serializable {
    private UUID code;
    private UUID storeId;
    private LocalDateTime orderTime;
    private OrderStatus status;
    private String phoneNumber;
    private String specialInstructions;
    private List<CreateOrderMenuRequest> orderMenus;
}
