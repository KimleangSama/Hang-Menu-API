package com.keakimleang.digital_menu.features.menus.payloads.requests;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class MenuToggleRequest {
    private UUID menuId;
    private UUID storeId;
}
