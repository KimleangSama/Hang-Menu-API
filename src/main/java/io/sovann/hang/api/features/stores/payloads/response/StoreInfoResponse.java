package io.sovann.hang.api.features.stores.payloads.response;

import io.sovann.hang.api.features.stores.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class StoreInfoResponse {
    private List<OperatingHourResponse> operatingHours;
    private List<OrderingOptionResponse> orderOptions;
    private List<PaymentMethodResponse> paymentMethods;

    public static StoreInfoResponse fromEntity(Store store) {
        StoreInfoResponse response = new StoreInfoResponse();
        response.setOperatingHours(OperatingHourResponse.fromEntities(store.getOperatingHours()));
        response.setOrderOptions(OrderingOptionResponse.fromEntities(store.getOrderingOptions()));
        response.setPaymentMethods(PaymentMethodResponse.fromEntities(store.getPaymentMethods()));
        return response;
    }
}
