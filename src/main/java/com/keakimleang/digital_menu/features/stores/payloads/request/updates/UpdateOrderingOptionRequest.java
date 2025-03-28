package com.keakimleang.digital_menu.features.stores.payloads.request.updates;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class UpdateOrderingOptionRequest {
    private UUID id;
    private String name;
    private String description;
    private List<UpdateFeeRangeRequest> feeRanges;
}
