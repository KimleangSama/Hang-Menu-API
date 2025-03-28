package com.keakimleang.digital_menu.features.stores.payloads.request.updates;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class UpdateFeeRangeRequest {
    private UUID id;
    private String condition;
    private Double fee;
}
