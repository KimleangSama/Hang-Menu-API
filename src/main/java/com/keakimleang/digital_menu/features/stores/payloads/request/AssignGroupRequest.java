package com.keakimleang.digital_menu.features.stores.payloads.request;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class AssignGroupRequest {
    private UUID groupId;
    private List<UUID> storeIds;
}
