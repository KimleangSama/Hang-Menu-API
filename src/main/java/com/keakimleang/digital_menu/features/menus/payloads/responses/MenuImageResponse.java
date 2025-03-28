package com.keakimleang.digital_menu.features.menus.payloads.responses;

import com.keakimleang.digital_menu.features.menus.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class MenuImageResponse {
    private UUID id;
    private String name;
    private String url;

    public static MenuImageResponse fromEntity(MenuImage menuImage) {
        MenuImageResponse response = new MenuImageResponse();
        response.setId(menuImage.getId());
        response.setName(menuImage.getName());
        response.setUrl(menuImage.getUrl());
        return response;
    }

    public static List<MenuImageResponse> fromEntities(List<MenuImage> menuImages) {
        return menuImages.stream().map(MenuImageResponse::fromEntity).toList();
    }
}
