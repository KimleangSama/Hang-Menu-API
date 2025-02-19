package io.sovann.hang.api.features.orders.payloads.requests;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class CreateOrderMenuRequest {
    private UUID menuId;
    private int quantity;
    private String specialRequests;
}
