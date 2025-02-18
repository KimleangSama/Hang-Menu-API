package io.sovann.hang.api.features.menus.payloads.responses;

import io.sovann.hang.api.features.menus.entities.MenuImage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

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
