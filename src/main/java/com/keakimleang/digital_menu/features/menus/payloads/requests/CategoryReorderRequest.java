package com.keakimleang.digital_menu.features.menus.payloads.requests;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CategoryReorderRequest {
    private UUID storeId;
    private List<CategoryPositionUpdate> categories;

    @Getter
    @Setter
    public static class CategoryPositionUpdate {
        private UUID id;
        private int position;
    }
}