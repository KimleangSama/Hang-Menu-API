package io.sovann.hang.api.features.carts.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class CartMenuMutateRequest {
    private UUID cartId;
    private UUID CartMenuId;
    private Integer quantity;
    private String specialRequests;
}
