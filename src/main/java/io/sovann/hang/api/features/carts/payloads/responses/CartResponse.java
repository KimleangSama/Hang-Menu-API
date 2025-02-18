package io.sovann.hang.api.features.carts.payloads.responses;

import io.sovann.hang.api.features.carts.entities.Cart;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CartResponse {
    private UUID id;
    private List<CartMenuResponse> CartMenus;

    public static CartResponse fromEntity(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setCartMenus(CartMenuResponse.fromEntities(cart.getCartMenus()));
        return response;
    }
}
