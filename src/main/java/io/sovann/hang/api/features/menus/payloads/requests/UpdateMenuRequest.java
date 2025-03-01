package io.sovann.hang.api.features.menus.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UpdateMenuRequest {
    private String code;
    private String name;
    private String description;
    private double price;
    private double discount;
    private String currency;
    private String image;
    private List<String> images;
    private List<String> badges;
    private UUID categoryId;
}
