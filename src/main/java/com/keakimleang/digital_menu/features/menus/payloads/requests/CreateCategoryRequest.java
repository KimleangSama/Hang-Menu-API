package com.keakimleang.digital_menu.features.menus.payloads.requests;

import com.keakimleang.digital_menu.features.menus.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateCategoryRequest {
    private String name;
    private String description;
    private String icon;
    private boolean isHidden = false;
    private boolean isAvailable = true;
    private UUID storeId;

    public static Category fromRequest(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setHidden(request.isHidden());
        category.setAvailable(request.isAvailable());
        return category;
    }
}
