package com.keakimleang.digital_menu.features.menus.payloads.requests;

import com.keakimleang.digital_menu.features.menus.entities.*;
import lombok.*;

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
