package com.keakimleang.digital_menu.features.stores.payloads.request;

import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class ExtendExpirationDateRequest {
    private UUID storeId;
    private LocalDateTime expiredAt;
    private String extendReason;
    private String slug;
}
