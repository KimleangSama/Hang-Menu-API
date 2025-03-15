package io.sovann.hang.api.features.menus.payloads.requests;

import io.sovann.hang.api.features.menus.entities.MenuImage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateMenuImageRequest {
    private String name;

    public static MenuImage fromRequest(String name) {
        MenuImage menuImage = new MenuImage();
        menuImage.setName(name);
        return menuImage;
    }
}
