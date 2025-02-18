package io.sovann.hang.api.features.orders.payloads.responses;

import io.sovann.hang.api.features.orders.entities.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class OrderResponse {
    private UUID id;
    private UUID storeId;
    private Double totalAmount;
    private String status;
    private String orderTime;
    private String specialInstructions;
    private List<OrderMenuResponse> orderMenus;

    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStoreId(order.getStore().getId());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setOrderTime(order.getOrderTime().toString());
        response.setSpecialInstructions(order.getSpecialInstructions());
        return response;
    }
}
