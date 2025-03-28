package com.keakimleang.digital_menu.features.menus.payloads.responses;

import com.keakimleang.digital_menu.features.menus.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class FavoriteResponse {
    private UUID menuId;
    private UUID userId;
    private String name;
    private String username;

    public static FavoriteResponse fromEntity(Favorite favorite) {
        FavoriteResponse response = new FavoriteResponse();
        response.setMenuId(favorite.getMenu().getId());
        response.setUserId(favorite.getUser().getId());
        response.setName(favorite.getMenu().getName());
        response.setUsername(favorite.getUser().getUsername());
        return response;
    }

    public static List<FavoriteResponse> fromEntities(List<Favorite> favorites) {
        return favorites.stream().map(FavoriteResponse::fromEntity).toList();
    }
}
