package io.sovann.hang.api.features.carts.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CreateCartRequest {
    private UUID tableId;
    private List<CreateCartMenuRequest> CartMenus;
}
