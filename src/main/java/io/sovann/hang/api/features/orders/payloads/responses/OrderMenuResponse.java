package io.sovann.hang.api.features.orders.payloads.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class OrderMenuResponse {
    private UUID id;
    private UUID orderId;
    private UUID menuId;
    private Integer quantity;
    private Double unitPrice;
    private String specialRequests;
}
