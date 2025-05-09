package com.keakimleang.digital_menu.features.stores.payloads.request;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import lombok.*;
import org.modelmapper.*;

@Getter
@Setter
@ToString
public class CreatePaymentMethodRequest {
    private String method;

    public static PaymentMethod fromRequest(CreatePaymentMethodRequest request) {
        ModelMapper mapper = MMConfig.mapper();
        return mapper.map(request, PaymentMethod.class);
    }

    public static List<PaymentMethod> fromRequests(List<CreatePaymentMethodRequest> paymentMethods, Store store) {
        ModelMapper mapper = MMConfig.mapper();
        return paymentMethods.stream()
                .map(oh -> {
                    PaymentMethod entity = mapper.map(oh, PaymentMethod.class);
                    entity.setStore(store);
                    return entity;
                })
                .toList();
    }
}
