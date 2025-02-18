package io.sovann.hang.api.features.orders.payloads.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
public class CreateOrderMenuRequest {
    private UUID menuId;
    private int quantity;
    private String specialRequests;
}
