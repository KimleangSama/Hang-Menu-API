package io.sovann.hang.api.features.menus.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.annotations.WithRateLimitProtection;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.menus.payloads.requests.CreateMenuRequest;
import io.sovann.hang.api.features.menus.payloads.requests.MenuToggleRequest;
import io.sovann.hang.api.features.menus.payloads.requests.UpdateMenuCategoryRequest;
import io.sovann.hang.api.features.menus.payloads.requests.UpdateMenuRequest;
import io.sovann.hang.api.features.menus.payloads.responses.CategoryMenuResponse;
import io.sovann.hang.api.features.menus.payloads.responses.MenuResponse;
import io.sovann.hang.api.features.menus.services.MenuServiceImpl;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.users.entities.Group;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.features.users.services.GroupServiceImpl;
import io.sovann.hang.api.features.users.services.UserServiceImpl;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(APIURLs.MENU)
@RequiredArgsConstructor
public class MenuController {
    private final MenuServiceImpl menuService;
    private final ControllerServiceCallback callback;
    private final UserServiceImpl userServiceImpl;
    private final GroupServiceImpl groupServiceImpl;

    @PostMapping("/create")
    public BaseResponse<MenuResponse> createMenu(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateMenuRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.createMenu(user.getUser(), request),
                "Menu failed to create",
                null);
    }

    @GetMapping("/of-category/{categoryId}/list")
    public BaseResponse<List<MenuResponse>> listMenusOfCategory(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID categoryId
    ) {
        return callback.execute(() -> menuService.listMenusOfCategoryId(user != null ? user.getUser() : null, categoryId),
                "Menu failed to list",
                null);
    }

    @WithRateLimitProtection
    @GetMapping("/of-store/{storeId}/all/without")
    public BaseResponse<List<MenuResponse>> listMenusOfStoreWithoutMappedCategory(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        return callback.execute(() -> menuService.listMenusWithCategory(user != null ? user.getUser() : null, storeId),
                "Menu failed to list", null);
    }

    // @WithRateLimitProtection
    @GetMapping("/of-store/{storeId}/all/with")
    public BaseResponse<List<CategoryMenuResponse>> listMenusOfStoreWithMappedCategory(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        return callback.execute(() -> {
            List<MenuResponse> responses = menuService.listMenusWithCategory(user != null ? user.getUser() : null, storeId);
            Map<UUID, List<MenuResponse>> grouped = responses.stream()
                    .collect(Collectors.groupingBy(MenuResponse::getCategoryId));
            return grouped.entrySet().stream()
                    .map(entry -> {
                        CategoryMenuResponse res = new CategoryMenuResponse();
                        res.setId(entry.getKey());
                        List<MenuResponse> menuList = entry.getValue();
                        menuList.sort(Comparator.comparingInt(MenuResponse::getPosition));
                        if (!menuList.isEmpty()) {
                            res.setName(menuList.getFirst().getCategoryName());
                            res.setPosition(menuList.getFirst().getPosition());
                        }
                        res.setMenus(menuList);
                        return res;
                    })
                    .sorted(Comparator.comparingInt(CategoryMenuResponse::getPosition))
                    .collect(Collectors.toList());
        }, "Menu failed to list", null);
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

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> deleteMenu(
            @CurrentUser CustomUserDetails user,
            @RequestBody MenuToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.deleteMenu(user.getUser(), request),
                "Menu failed to delete",
                null);
    }

    @GetMapping("/{id}/details")
    public BaseResponse<MenuResponse> getMenuResponseById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        return callback.execute(() -> menuService.getMenuResponseById(user != null ? user.getUser() : null, id),
                "Menu failed to get",
                null);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> updateMenu(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestBody UpdateMenuRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.updateMenu(user.getUser(), id, request),
                "Menu failed to update",
                null);
    }

    @PatchMapping("/{id}/update-category")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> updateMenuCategory(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestBody UpdateMenuCategoryRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);

        return callback.execute(() -> {
                    Group group = groupServiceImpl.getGroupOfUser(user.getUser());
                    Store store = userServiceImpl.getStoreOfGroup(group);
                    if (store == null) {
                        throw new RuntimeException("Store not found");
                    }
                    request.setStoreId(store.getId());
                    return menuService.updateMenuCategory(user.getUser(), id, request);
                },
                "Menu failed to update",
                null);
    }

    @PostMapping("/batch-create")
    public BaseResponse<String> batchMenuCreate(
            @CurrentUser CustomUserDetails user,
            @RequestParam("storeId") UUID storeId,
            @RequestParam("file") MultipartFile file) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> {
                    try {
                        return menuService.batchMenuCreate(user.getUser(), storeId, file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                "Menu failed to batch create",
                null);
    }
}
