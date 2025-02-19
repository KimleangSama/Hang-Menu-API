package io.sovann.hang.api.features.orders.payloads.responses;

import io.sovann.hang.api.features.orders.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class OrderMenuResponse {
    private UUID id;
    private UUID orderId;
    private UUID menuId;
    private Integer quantity;
    private double price;
    private String specialRequests;

    public static OrderMenuResponse fromEntity(OrderMenu orderMenu) {
        OrderMenuResponse response = new OrderMenuResponse();
        response.setId(orderMenu.getId());
        response.setOrderId(orderMenu.getOrder().getId());
        response.setMenuId(orderMenu.getMenu().getId());
        response.setQuantity(orderMenu.getQuantity());
        response.setPrice(orderMenu.getMenu().getPrice());
        response.setSpecialRequests(orderMenu.getSpecialRequests());
        return response;
    }

    public static List<OrderMenuResponse> fromEntities(List<OrderMenu> cartMenus) {
        return cartMenus.stream()
                .map(OrderMenuResponse::fromEntity)
                .toList();
    }
}
