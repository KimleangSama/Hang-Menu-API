package com.keakimleang.digital_menu.features.stores.payloads.request.updates;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class UpdateOperatingHourRequest {
    private UUID id;
    private String day;
    private String openTime;
    private String closeTime;
}
