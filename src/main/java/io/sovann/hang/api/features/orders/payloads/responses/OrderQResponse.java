package io.sovann.hang.api.features.orders.payloads.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class OrderQResponse {
    private UUID code;
    private String message;
    private String statusCode;
}
