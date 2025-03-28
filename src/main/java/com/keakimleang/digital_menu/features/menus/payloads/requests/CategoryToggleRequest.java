package com.keakimleang.digital_menu.features.menus.payloads.requests;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CategoryToggleRequest {
    private UUID categoryId;
    private UUID storeId;
}
