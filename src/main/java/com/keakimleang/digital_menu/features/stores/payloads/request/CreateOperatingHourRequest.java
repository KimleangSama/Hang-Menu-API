package com.keakimleang.digital_menu.features.stores.payloads.request;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.modelmapper.*;

@Slf4j
@Getter
@Setter
@ToString
public class CreateOperatingHourRequest {
    private String day;
    private String openTime;
    private String closeTime;

    public static OperatingHour fromRequest(CreateOperatingHourRequest request) {
        ModelMapper mapper = MMConfig.mapper();
        return mapper.map(request, OperatingHour.class);
    }

    public static List<OperatingHour> fromRequests(List<CreateOperatingHourRequest> operatingHours, Store store) {
        ModelMapper mapper = MMConfig.mapper();
        return operatingHours.stream()
                .map(oh -> {
                    OperatingHour entity = mapper.map(oh, OperatingHour.class);
                    entity.setStore(store);
                    return entity;
                })
                .toList();
    }
}
