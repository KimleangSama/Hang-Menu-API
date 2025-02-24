package io.sovann.hang.api.features.menus.payloads.responses;

import io.sovann.hang.api.features.menus.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class MenuResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private double price;
    private double discount;
    private String currency;
    private String image;
    private boolean isHidden;
    private boolean isAvailable;
    private boolean isDeleted;
    private boolean isFavorite;

    private UUID categoryId;
    private String categoryName;

    private List<MenuImageResponse> images;

    public static MenuResponse fromEntity(Menu menu) {
        MenuResponse response = new MenuResponse();
        response.setId(menu.getId());
        response.setCode(menu.getCode());
        response.setName(menu.getName());
        response.setDescription(menu.getDescription());
        response.setPrice(menu.getPrice());
        response.setDiscount(menu.getDiscount());
        response.setCurrency(menu.getCurrency());
        response.setImage(menu.getImage());
        response.setHidden(menu.getIsHidden());
        response.setAvailable(menu.getIsAvailable());
        if (menu.getImages() != null && !menu.getImages().isEmpty()) {
            response.setImages(MenuImageResponse.fromEntities(menu.getImages()));
        }
        if (menu.getCategory() == null) {
            return response;
        }
        response.setCategoryId(menu.getCategory().getId());
        response.setCategoryName(menu.getCategory().getName());
        return response;
    }

    public static List<MenuResponse> fromEntities(List<Menu> menus, List<FavoriteResponse> favorites) {
        List<MenuResponse> responses = new ArrayList<>();
        for (Menu menu : menus) {
            MenuResponse response = fromEntity(menu);
            response.setFavorite(
                    favorites
                            .stream()
                            .anyMatch(favorite -> favorite.getMenuId().equals(menu.getId())));
            responses.add(response);
        }
        return responses;
    }
}
