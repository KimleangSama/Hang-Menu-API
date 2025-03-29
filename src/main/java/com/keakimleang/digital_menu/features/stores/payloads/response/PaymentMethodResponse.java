package com.keakimleang.digital_menu.features.stores.payloads.response;

import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import java.util.stream.*;
import lombok.*;

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
