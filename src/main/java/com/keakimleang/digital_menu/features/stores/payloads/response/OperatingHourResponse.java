package com.keakimleang.digital_menu.features.stores.payloads.response;

import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import java.util.stream.*;
import lombok.*;

@Getter
@Setter
@ToString
public class OperatingHourResponse {
    private UUID id;
    private String day;
    private String openTime;
    private String closeTime;

    public static OperatingHourResponse fromEntity(OperatingHour operatingHour) {
        OperatingHourResponse response = new OperatingHourResponse();
        response.setId(operatingHour.getId());
        response.setDay(operatingHour.getDay());
        response.setOpenTime(operatingHour.getOpenTime());
        response.setCloseTime(operatingHour.getCloseTime());
        return response;
    }

    public static List<OperatingHourResponse> fromEntities(List<OperatingHour> operatingHours) {
        if (operatingHours == null) {
            return List.of();
        }
        return operatingHours.stream().map(OperatingHourResponse::fromEntity).collect(Collectors.toList());
    }
}
