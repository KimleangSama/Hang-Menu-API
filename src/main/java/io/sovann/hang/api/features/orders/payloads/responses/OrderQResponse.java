package io.sovann.hang.api.features.orders.payloads.responses;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class OrderQResponse {
    private UUID code;
    private String message;
    private String statusCode;
}
