package io.sovann.hang.api.features.carts.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.carts.payloads.requests.CreateCartRequest;
import io.sovann.hang.api.features.carts.payloads.responses.CartResponse;
import io.sovann.hang.api.features.carts.services.CartServiceImpl;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(APIURLs.CART)
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImpl cartService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    public BaseResponse<CartResponse> createCart(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateCartRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> cartService.createCart(user.getUser(), request),
                "Cart failed to create",
                null);
    }

    @GetMapping("/get")
    public BaseResponse<CartResponse> getCart(
            @CurrentUser CustomUserDetails user
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> cartService.getCart(user.getUser()),
                "Cart failed to get",
                null);
    }

    @GetMapping("/{cartId}/get")
    public BaseResponse<CartResponse> getCartById(
            @PathVariable UUID cartId
    ) {
        return callback.execute(() -> cartService.getCartById(cartId),
                "Cart failed to get",
                null);
    }
}
