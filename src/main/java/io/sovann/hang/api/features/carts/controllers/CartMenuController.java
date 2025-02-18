package io.sovann.hang.api.features.carts.controllers;

import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.carts.payloads.requests.CartMenuMutateRequest;
import io.sovann.hang.api.features.carts.payloads.requests.CreateCartMenuRequest;
import io.sovann.hang.api.features.carts.payloads.responses.CartMenuResponse;
import io.sovann.hang.api.features.carts.services.CartMenuServiceImpl;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(APIURLs.CART_MENU)
@RequiredArgsConstructor
public class CartMenuController {
    private final CartMenuServiceImpl CartMenuService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    public BaseResponse<CartMenuResponse> createCart(
            @RequestBody CreateCartMenuRequest request
    ) {
        return callback.execute(() -> CartMenuService.createCart(request),
                "CartMenu failed to create",
                null);
    }

    @GetMapping("/of-cart/{cartId}/list")
    public BaseResponse<List<CartMenuResponse>> getCartMenuOfCart(
            @PathVariable UUID cartId
    ) {
        return callback.execute(() -> CartMenuService.getCartMenuOfCart(cartId),
                "CartMenu failed to get",
                null);
    }

    @DeleteMapping("/delete")
    public BaseResponse<CartMenuResponse> deleteCartMenu(
            @RequestBody CartMenuMutateRequest request
    ) {
        return callback.execute(() -> CartMenuService.deleteCartMenu(request),
                "CartMenu failed to delete",
                null);
    }

    @PostMapping("/get")
    public BaseResponse<CartMenuResponse> getCartMenuById(
            @RequestBody CartMenuMutateRequest request
    ) {
        return callback.execute(() -> CartMenuService.getCartMenuById(request),
                "Cart failed to get",
                null);
    }

    @PatchMapping("/update")
    public BaseResponse<CartMenuResponse> updateCartMenu(
            @RequestBody CartMenuMutateRequest request
    ) {
        return callback.execute(() -> CartMenuService.updateCartMenu(request),
                "CartMenu failed to update",
                null);
    }
}
