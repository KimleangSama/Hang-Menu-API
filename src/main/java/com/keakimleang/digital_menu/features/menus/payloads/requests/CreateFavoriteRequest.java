package com.keakimleang.digital_menu.features.menus.payloads.requests;

import com.keakimleang.digital_menu.features.menus.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateFavoriteRequest {
    private UUID menuId;
    private UUID categoryId;

    public static Favorite fromRequest(Menu menu) {
        Favorite favorite = new Favorite();
        favorite.setMenu(menu);
        return favorite;
    }
}
