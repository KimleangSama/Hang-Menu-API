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
    private String menuName;
    private String menuImage;
    private Integer quantity;
    private double price;
    private double discount;
    private String currency;
    private double totalAmount;
    private String specialRequests;

    public static OrderMenuResponse fromEntity(OrderMenu orderMenu) {
        OrderMenuResponse response = new OrderMenuResponse();
        response.setId(orderMenu.getId());
        response.setOrderId(orderMenu.getOrder().getId());
        response.setMenuId(orderMenu.getId());
        response.setMenuName(orderMenu.getName());
        response.setMenuImage(orderMenu.getImage());
        response.setPrice(orderMenu.getPrice());
        response.setDiscount(orderMenu.getDiscount());
        response.setQuantity(orderMenu.getQuantity());
        response.setCurrency(orderMenu.getCurrency());
        response.setTotalAmount((orderMenu.getPrice() - orderMenu.getDiscount()) * orderMenu.getQuantity());
        response.setSpecialRequests(orderMenu.getSpecialRequests());
        return response;
    }

    public static List<OrderMenuResponse> fromEntities(List<OrderMenu> orderMenus) {
        if (orderMenus == null) {
            return Collections.emptyList();
        }
        return orderMenus.stream()
                .map(OrderMenuResponse::fromEntity)
                .toList();
    }
}
