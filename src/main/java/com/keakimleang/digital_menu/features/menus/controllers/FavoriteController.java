package com.keakimleang.digital_menu.features.menus.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.menus.payloads.requests.*;
import com.keakimleang.digital_menu.features.menus.payloads.responses.*;
import com.keakimleang.digital_menu.features.menus.services.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.FAVORITE)
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteServiceImpl favoriteService;
    private final ControllerServiceCallback callback;

    @PostMapping("/favorite")
    @PreAuthorize("authenticated")
    public BaseResponse<FavoriteResponse> createMenuFavorite(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateFavoriteRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.user());
        return callback.execute(() -> favoriteService.createFavorite(user.user(), request),
                "Favorite failed to create",
                null);
    }

    @PostMapping("/unfavorite")
    @PreAuthorize("authenticated")
    public BaseResponse<FavoriteResponse> deleteMenuFavorite(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateFavoriteRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.user());
        return callback.execute(() -> favoriteService.deleteFavorite(user.user(), request),
                "Favorite failed to delete",
                null);
    }

    @GetMapping("/list")
    @PreAuthorize("authenticated")
    public BaseResponse<List<FavoriteResponse>> listMenuFavorites(
            @CurrentUser CustomUserDetails user
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.user());
        return callback.execute(() -> favoriteService.listMenuFavorites(user.user()),
                "Favorite failed to list",
                null);
    }
}
