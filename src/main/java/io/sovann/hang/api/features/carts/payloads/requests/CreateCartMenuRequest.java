package io.sovann.hang.api.features.carts.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CreateCartMenuRequest implements Serializable {
    private UUID cartId;
    private UUID menuId;
    private Integer quantity;
    private String specialRequests;
}
