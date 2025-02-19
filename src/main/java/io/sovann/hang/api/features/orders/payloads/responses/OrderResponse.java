package io.sovann.hang.api.features.orders.payloads.responses;

import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.enums.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class OrderResponse {
    private UUID id;
    private UUID storeId;
    private Double totalAmount;
    private OrderStatus status;
    private String orderTime;
    private String phoneNumber;
    private String specialInstructions;
    private List<OrderMenuResponse> orderMenus;

    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStoreId(order.getStore().getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setOrderTime(order.getOrderTime().toString());
        response.setPhoneNumber(order.getPhoneNumber());
        response.setSpecialInstructions(order.getSpecialInstructions());
        response.setOrderMenus(OrderMenuResponse.fromEntities(order.getOrderMenus()));
        return response;
    }
}
