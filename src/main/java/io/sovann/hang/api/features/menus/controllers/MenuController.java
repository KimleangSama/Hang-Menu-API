package io.sovann.hang.api.features.menus.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.commons.controllers.*;
import io.sovann.hang.api.features.commons.payloads.*;
import io.sovann.hang.api.features.menus.payloads.requests.*;
import io.sovann.hang.api.features.menus.payloads.responses.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.utils.*;
import java.util.*;
import java.util.stream.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(APIURLs.MENU)
@RequiredArgsConstructor
public class MenuController {
    private final MenuServiceImpl menuService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    public BaseResponse<MenuResponse> createMenu(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateMenuRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> menuService.createMenu(user.getUser(), request),
                "Menu failed to create",
                null);
    }

    @GetMapping("/list")
    public BaseResponse<List<MenuResponse>> listMenusByCategoryId(
            @CurrentUser CustomUserDetails user,
            @RequestParam UUID categoryId
    ) {
        return callback.execute(() -> menuService.listMenuByCategoryId(user != null ? user.getUser() : null, categoryId),
                "Menu failed to list",
                null);
    }

    @WithRateLimitProtection
    @GetMapping("/of-store/{storeId}/list")
    public BaseResponse<List<MenuResponse>> listMenus(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        return callback.execute(() -> menuService.listMenusWithCategory(user != null ? user.getUser() : null, storeId),
                "Menu failed to list", null);
    }

    @WithRateLimitProtection
    @GetMapping("/list/{storeId}/all/category")
    public BaseResponse<List<CategoryMenuResponse>> listMenusWithCategory(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        return callback.execute(() -> {
                    List<MenuResponse> responses = menuService.listMenusWithCategory(user != null ? user.getUser() : null, storeId);
                    Map<UUID, List<MenuResponse>> grouped = responses.stream().collect(Collectors.groupingBy(MenuResponse::getCategoryId));
                    return grouped.entrySet().stream().map(entry -> {
                        CategoryMenuResponse categoryMenuResponse = new CategoryMenuResponse();
                        categoryMenuResponse.setId(entry.getKey());
                        if (!entry.getValue().isEmpty()) {
                            categoryMenuResponse.setName(entry.getValue().getFirst().getCategoryName());
                        }
                        categoryMenuResponse.setMenus(entry.getValue());
                        return categoryMenuResponse;
                    }).collect(Collectors.toList());
                }, "Menu failed to list",
                null);
    }

    @PatchMapping("/toggle-visibility")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> toggleMenuVisibility(
            @CurrentUser CustomUserDetails user,
            @RequestBody MenuToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> menuService.toggleMenuVisibility(user.getUser(), request),
                "Menu failed to set hide",
                null);
    }

    @PatchMapping("/toggle-availability")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> toggleMenuAvailability(
            @CurrentUser CustomUserDetails user,
            @RequestBody MenuToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> menuService.toggleMenuAvailability(user.getUser(), request),
                "Menu failed to set availability",
                null);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> deleteMenu(
            @CurrentUser CustomUserDetails user,
            @RequestBody MenuToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> menuService.deleteMenu(user.getUser(), request),
                "Menu failed to delete",
                null);
    }

    @GetMapping("/{id}/get")
    public BaseResponse<MenuResponse> getMenuResponseById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        return callback.execute(() -> menuService.getMenuResponseById(user != null ? user.getUser() : null, id),
                "Menu failed to get",
                null);
    }

    @PatchMapping("/{id}/update-image")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> updateMenuImage(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestBody String image
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.updateMenuImage(id, image),
                "Menu failed to update image",
                null);
    }
}
