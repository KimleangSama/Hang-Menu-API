package com.keakimleang.digital_menu.features.stores.payloads.request.updates;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class UpdatePaymentMethodRequest {
    private UUID id;
    private String method;
}
