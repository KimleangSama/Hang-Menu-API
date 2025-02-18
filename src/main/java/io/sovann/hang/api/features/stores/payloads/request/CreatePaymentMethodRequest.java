package io.sovann.hang.api.features.stores.payloads.request;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.stores.entities.PaymentMethod;
import io.sovann.hang.api.features.stores.entities.Store;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.List;

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
