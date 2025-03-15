package io.sovann.hang.api.features.menus.payloads.requests;

import io.sovann.hang.api.features.menus.entities.Menu;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

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
    private boolean isHidden = false;
    private List<String> images;
    private List<String> badges;

    private UUID createdBy;

    private UUID storeId;
    private UUID categoryId;

    public static Menu fromRequest(CreateMenuRequest request) {
        Menu menu = new Menu();
        menu.setCode(request.getCode());
        menu.setName(request.getName());
        menu.setDescription(request.getDescription());
        menu.setPrice(request.getPrice());
        menu.setDiscount(request.getDiscount());
        menu.setCurrency(request.getCurrency());
        menu.setImage(request.getImage());
        menu.setIsHidden(request.isHidden());
        menu.setBadges(request.getBadges());
        return menu;
    }
}
