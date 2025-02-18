package io.sovann.hang.api.features.menus.payloads.requests;

import io.sovann.hang.api.features.menus.entities.Favorite;
import io.sovann.hang.api.features.menus.entities.Menu;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

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
