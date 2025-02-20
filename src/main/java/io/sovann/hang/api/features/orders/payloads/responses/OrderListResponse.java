package io.sovann.hang.api.features.orders.payloads.responses;

import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.enums.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class OrderListResponse {
    private UUID id;
    private UUID storeId;
    private Double totalAmount;
    private OrderStatus status;
    private String orderTime;
    private String phoneNumber;
    private String specialInstructions;

    public static OrderListResponse fromEntity(Order order) {
        OrderListResponse response = new OrderListResponse();
        response.setId(order.getId());
        response.setStoreId(order.getStore().getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setOrderTime(order.getOrderTime().toString());
        response.setPhoneNumber(order.getPhoneNumber());
        response.setSpecialInstructions(order.getSpecialInstructions());
        return response;
    }

    public static List<OrderListResponse> fromEntities(List<Order> orders) {
        List<OrderListResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(fromEntity(order));
        }
        return responses;
    }
}
