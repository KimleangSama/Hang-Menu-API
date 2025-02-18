package io.sovann.hang.api.features.stores.payloads.response;

import io.sovann.hang.api.features.stores.entities.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class PaymentMethodResponse {
    private UUID id;
    private String method;

    public static PaymentMethodResponse fromEntity(PaymentMethod paymentMethod) {
        PaymentMethodResponse response = new PaymentMethodResponse();
        response.setId(paymentMethod.getId());
        response.setMethod(paymentMethod.getMethod());
        return response;
    }

    public static List<PaymentMethodResponse> fromEntities(List<PaymentMethod> paymentMethods) {
        if (paymentMethods == null) {
            return List.of();
        }
        return paymentMethods.stream().map(PaymentMethodResponse::fromEntity).collect(Collectors.toList());
    }
}
