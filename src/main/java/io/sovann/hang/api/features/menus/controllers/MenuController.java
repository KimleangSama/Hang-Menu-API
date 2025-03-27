package io.sovann.hang.api.features.menus.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.commons.controllers.*;
import io.sovann.hang.api.commons.payloads.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.menus.payloads.requests.*;
import io.sovann.hang.api.features.menus.payloads.responses.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.features.users.services.*;
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
        return callback.execute(() -> menuService.create(user.getUser(), request),
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
        return callback.execute(() -> menuService.findAllMenusByStoreId(user != null ? user.getUser() : null, storeId),
                "Menu failed to list", null);
    }

    @WithRateLimitProtection
    @GetMapping("/of-store/{storeId}/all/with")
    public BaseResponse<List<CategoryMenuResponse>> findAllMenusByStoreIdWithMapping(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        return callback.execute(() -> {
            List<MenuResponse> responses = menuService.findAllMenusByStoreId(user != null ? user.getUser() : null, storeId);
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
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> menuService.toggleMenuVisibility(user.getUser(), request),
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
        return callback.execute(() -> menuService.deleteMenuById(user.getUser(), request),
                "Menu failed to delete",
                null);
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<MenuResponse> findMenuById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        return callback.execute(() -> menuService.findMenuById(user != null ? user.getUser() : null, id),
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
        return callback.execute(() -> menuService.updateMenuById(user.getUser(), id, request),
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
        return callback.execute(() -> menuService.updateCategoryOfMenuById(user.getUser(), id, request),
                "Menu failed to update",
                null);
    }
}
