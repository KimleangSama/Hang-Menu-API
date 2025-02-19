package io.sovann.hang.api.features.orders.payloads.requests;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class OrderMenuMutateRequest {
    private UUID orderId;
    private UUID orderMenuId;
    private int quantity;
    private String specialRequests;
}
