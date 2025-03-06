package io.sovann.hang.api.features.orders.payloads.responses;

import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.enums.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
@Setter
@ToString
public class OrderResponse {
    private UUID id;
    private UUID storeId;
    private UUID code;
    private Double totalAmountInRiel;
    private Double totalAmountInDollar;
    private OrderStatus status;
    private String orderTime;
    private String phoneNumber;
    private String specialInstructions;
    private List<OrderMenuResponse> orderMenus;

    public static OrderResponse fromEntity(Order order, boolean showOrderDetails) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStoreId(order.getStore().getId());
        response.setCode(order.getCode());
        response.setTotalAmountInRiel(order.getTotalAmountInRiel());
        response.setTotalAmountInDollar(order.getTotalAmountInDollar());
        response.setStatus(order.getStatus());
        response.setOrderTime(order.getOrderTime().toString());
        response.setPhoneNumber(order.getPhoneNumber());
        response.setSpecialInstructions(order.getSpecialInstructions());
        if (showOrderDetails) {
            response.setOrderMenus(OrderMenuResponse.fromEntities(order.getOrderMenus()));
        }
        return response;
    }

    public static List<OrderResponse> fromEntities(List<Order> orders, boolean showOrderDetails) {
        List<OrderResponse> responses = new ArrayList<>();
        for (Order order : orders) {
            responses.add(fromEntity(order, showOrderDetails));
        }
        return responses;
    }
}
