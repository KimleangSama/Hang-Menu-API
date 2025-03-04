package io.sovann.hang.api.features.menus.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.menus.payloads.requests.CreateFavoriteRequest;
import io.sovann.hang.api.features.menus.payloads.responses.FavoriteResponse;
import io.sovann.hang.api.features.menus.services.FavoriteServiceImpl;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> favoriteService.createFavorite(user.getUser(), request),
                "Favorite failed to create",
                null);
    }

    @PostMapping("/unfavorite")
    @PreAuthorize("authenticated")
    public BaseResponse<FavoriteResponse> deleteMenuFavorite(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateFavoriteRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> favoriteService.deleteFavorite(user.getUser(), request),
                "Favorite failed to delete",
                null);
    }

    @GetMapping("/list")
    @PreAuthorize("authenticated")
    public BaseResponse<List<FavoriteResponse>> listMenuFavorites(
            @CurrentUser CustomUserDetails user
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> favoriteService.listMenuFavorites(user.getUser()),
                "Favorite failed to list",
                null);
    }
}
