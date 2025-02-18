package io.sovann.hang.api.features.stores.payloads.response;

import io.sovann.hang.api.features.stores.entities.OperatingHour;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
