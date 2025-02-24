package io.sovann.hang.api.features.menus.payloads.requests;

import io.sovann.hang.api.features.menus.entities.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateMenuImageRequest {
    private String name;
    private String url;

    public static MenuImage fromRequest(CreateMenuImageRequest request) {
        MenuImage menuImage = new MenuImage();
        menuImage.setName(request.getName());
        menuImage.setUrl(request.getUrl());
        return menuImage;
    }
}
