package io.sovann.hang.api.features.carts.payloads.responses;

import io.sovann.hang.api.features.carts.entities.CartMenu;
import io.sovann.hang.api.features.menus.payloads.responses.MenuResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CartMenuResponse {
    private UUID id;
    private UUID cartId;
    private UUID menuId;
    private int quantity;
    private String specialRequests;
    private MenuResponse menu;

    public static CartMenuResponse fromEntity(CartMenu cartMenu) {
        CartMenuResponse response = new CartMenuResponse();
        response.setId(cartMenu.getId());
        response.setCartId(cartMenu.getCart().getId());
        response.setMenu(MenuResponse.fromEntity(cartMenu.getMenu()));
        response.setMenuId(cartMenu.getMenu().getId());
        response.setQuantity(cartMenu.getQuantity());
        response.setSpecialRequests(cartMenu.getSpecialRequests());
        return response;
    }

    public static List<CartMenuResponse> fromEntities(List<CartMenu> cartMenus) {
        if (cartMenus == null) {
            return Collections.emptyList();
        }
        List<CartMenuResponse> responses = new ArrayList<>();
        for (CartMenu cartMenu : cartMenus) {
            responses.add(fromEntity(cartMenu));
        }
        return responses;
    }
}
