package io.sovann.hang.api.features.stores.payloads.request;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.stores.entities.OperatingHour;
import io.sovann.hang.api.features.stores.entities.Store;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.List;

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
