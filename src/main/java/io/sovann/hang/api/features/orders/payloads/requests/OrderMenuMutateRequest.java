package io.sovann.hang.api.features.orders.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class OrderMenuMutateRequest {
    private UUID orderId;
    private UUID orderMenuId;
    private int quantity;
    private String specialRequests;
}
