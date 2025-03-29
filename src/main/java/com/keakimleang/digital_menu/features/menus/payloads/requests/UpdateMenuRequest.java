package com.keakimleang.digital_menu.features.menus.payloads.requests;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class UpdateMenuRequest {
    private String code;
    private String name;
    private String description;
    private double price;
    private double discount;
    private boolean hidden;
    private String currency;
    private String image;
    private List<String> images;
    private List<String> badges;
    private UUID categoryId;
    private UUID storeId;
}
