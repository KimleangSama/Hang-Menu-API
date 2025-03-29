package com.keakimleang.digital_menu.features.menus.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.menus.payloads.requests.*;
import com.keakimleang.digital_menu.features.menus.payloads.responses.*;
import com.keakimleang.digital_menu.features.menus.services.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.features.users.services.*;
import com.keakimleang.digital_menu.utils.*;
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
    private final UserServiceImpl userServiceImpl;
    private final GroupServiceImpl groupServiceImpl;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> createMenu(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateMenuRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.create(user.user(), request),
                "Menu failed to create",
                null);
    }

    @WithRateLimitProtection
    @GetMapping("/of-store/{storeId}/all/without")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<List<MenuResponse>> findAllMenusByStoreIdWithoutMapping(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        return callback.execute(() -> menuService.findAllMenusByStoreId(user != null ? user.user() : null, storeId),
                "Menu failed to list", null);
    }

    @WithRateLimitProtection
    @GetMapping("/of-store/{storeId}/all/with")
    public BaseResponse<List<CategoryMenuResponse>> findAllMenusByStoreIdWithMapping(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        return callback.execute(() -> {
            List<MenuResponse> responses = menuService.findAllMenusByStoreId(user != null ? user.user() : null, storeId);
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

    @PatchMapping("/toggle-availability")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> toggleMenuAvailability(
            @CurrentUser CustomUserDetails user,
            @RequestBody MenuToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.toggleMenuAvailability(user.user(), request),
                "Menu failed to set hide",
                null);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> deleteMenuById(
            @CurrentUser CustomUserDetails user,
            @RequestBody MenuToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.deleteMenuById(user.user(), request),
                "Menu failed to delete",
                null);
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> findMenuById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        return callback.execute(() -> menuService.findMenuById(user != null ? user.user() : null, id),
                "Menu failed to get",
                null);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> updateMenuById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestBody UpdateMenuRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.updateMenuById(user.user(), id, request),
                "Menu failed to update",
                null);
    }

    @PatchMapping("/{id}/update-category")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> updateCategoryOfMenuById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestBody UpdateMenuCategoryRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.updateCategoryOfMenuById(user.user(), id, request),
                "Menu failed to update",
                null);
    }
}
