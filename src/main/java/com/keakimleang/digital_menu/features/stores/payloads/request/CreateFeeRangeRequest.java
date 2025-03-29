package com.keakimleang.digital_menu.features.stores.payloads.request;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import lombok.*;
import org.modelmapper.*;

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
