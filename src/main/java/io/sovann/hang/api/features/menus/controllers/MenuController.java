package io.sovann.hang.api.features.menus.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.commons.payloads.PageMeta;
import io.sovann.hang.api.features.menus.payloads.requests.CreateMenuRequest;
import io.sovann.hang.api.features.menus.payloads.requests.MenuToggleRequest;
import io.sovann.hang.api.features.menus.payloads.responses.CategoryMenuResponse;
import io.sovann.hang.api.features.menus.payloads.responses.MenuResponse;
import io.sovann.hang.api.features.menus.services.MenuServiceImpl;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/list/all")
    public BaseResponse<List<MenuResponse>> listMenus(
            @CurrentUser CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageMeta meta = new PageMeta(page, size, menuService.count());
        return callback.execute(() -> menuService.listMenus(user != null ? user.getUser() : null, page, size),
                "Menu failed to list",
                meta);
    }

    @GetMapping("/list/{storeId}/all/category")
    public BaseResponse<List<CategoryMenuResponse>> listMenusWithCategory(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        return callback.execute(() -> {
                    List<MenuResponse> responses = menuService.listMenusWithCategory(user != null ? user.getUser() : null, storeId);
                    Map<UUID, List<MenuResponse>> grouped = responses.stream().collect(Collectors.groupingBy(MenuResponse::getCategoryId));
                    log.info("Grouped: {}", grouped);
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
}
