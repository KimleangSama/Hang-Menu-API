package io.sovann.hang.api.features.stores.payloads.request;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.stores.entities.FeeRange;
import io.sovann.hang.api.features.stores.entities.OrderingOption;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.List;

@Getter
@Setter
@ToString
public class CreateFeeRangeRequest {
    private String condition;
    private Double fee;

    public static FeeRange fromRequest(CreateFeeRangeRequest request) {
        ModelMapper mapper = MMConfig.mapper();
        return mapper.map(request, FeeRange.class);
    }


    public static List<FeeRange> fromRequests(OrderingOption orderingOption, List<FeeRange> feeRanges) {
        ModelMapper mapper = MMConfig.mapper();
        return feeRanges.stream()
                .map(fr -> {
                    FeeRange entity = mapper.map(fr, FeeRange.class);
                    entity.setOrderingOption(orderingOption);
                    return entity;
                })
                .toList();
    }
}
