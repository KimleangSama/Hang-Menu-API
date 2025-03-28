package com.keakimleang.digital_menu.features.stores.payloads.response;

import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class OrderingOptionResponse {
    private UUID id;
    private String name;
    private String description;
    private List<FeeRangeResponse> feeRanges;

    public static OrderingOptionResponse fromEntity(OrderingOption orderingOption) {
        OrderingOptionResponse response = new OrderingOptionResponse();
        response.setId(orderingOption.getId());
        response.setName(orderingOption.getName());
        response.setDescription(orderingOption.getDescription());
        response.setFeeRanges(FeeRangeResponse.fromEntities(orderingOption.getFeeRanges()));
        return response;
    }

    public static List<OrderingOptionResponse> fromEntities(List<OrderingOption> orderingOptions) {
        if (orderingOptions == null) {
            return Collections.emptyList();
        }
        return orderingOptions.stream().map(OrderingOptionResponse::fromEntity).toList();
    }
}
