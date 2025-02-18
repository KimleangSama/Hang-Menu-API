package io.sovann.hang.api.features.stores.payloads.response;

import io.sovann.hang.api.features.stores.entities.OrderingOption;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
