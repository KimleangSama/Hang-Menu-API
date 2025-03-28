package com.keakimleang.digital_menu.features.stores.payloads.request;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import lombok.*;
import org.modelmapper.*;

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
