package io.sovann.hang.api.features.stores.payloads.request.updates;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class UpdatePaymentMethodRequest {
    private UUID id;
    private String method;
}
