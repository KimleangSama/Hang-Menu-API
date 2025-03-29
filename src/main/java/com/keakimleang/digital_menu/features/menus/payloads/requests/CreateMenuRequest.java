package com.keakimleang.digital_menu.features.menus.payloads.requests;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.menus.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateMenuRequest {
    private String code;
    private String name;
    private String description;
    private double price;
    private double discount;
    private String currency;
    private String image;
    private boolean isAvailable;
    private List<String> images;
    private List<String> badges;

    private UUID createdBy;

    private UUID storeId;
    private UUID categoryId;

    public static Menu fromRequest(CreateMenuRequest request) {
        Menu menu = new Menu();
        MMConfig.mapper().map(request, menu);
        menu.setBadges(request.getBadges());
        return menu;
    }
}
