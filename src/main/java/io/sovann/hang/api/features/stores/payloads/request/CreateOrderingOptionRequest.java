package io.sovann.hang.api.features.stores.payloads.request;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.stores.entities.OrderingOption;
import io.sovann.hang.api.features.stores.entities.Store;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter
@Setter
@ToString
public class CreateOrderingOptionRequest {
    private String name;
    private String description;
    private List<CreateFeeRangeRequest> feeRanges;

    public static OrderingOption fromRequest(CreateOrderingOptionRequest request) {
        ModelMapper mapper = MMConfig.mapper();
        return mapper.map(request, OrderingOption.class);
    }

    public static List<OrderingOption> fromRequests(List<CreateOrderingOptionRequest> orderOptions, Store store) {
        ModelMapper mapper = MMConfig.mapper();
        return orderOptions.stream()
                .map(oo -> {
                    OrderingOption entity = mapper.map(oo, OrderingOption.class);
                    entity.setStore(store);
                    return entity;
                })
                .toList();
    }
}
