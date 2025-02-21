package io.sovann.hang.api.features.menus.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.commons.controllers.*;
import io.sovann.hang.api.features.commons.payloads.*;
import io.sovann.hang.api.features.menus.payloads.requests.*;
import io.sovann.hang.api.features.menus.payloads.responses.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.users.entities.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.CATEGORY)
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryServiceImpl categoryService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<CategoryResponse> createCategory(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateCategoryRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> categoryService.createCategory(user.getUser(), request),
                "Category failed to create",
                null);
    }

    @GetMapping("/of-store/{storeId}/list")
    public BaseResponse<List<CategoryResponse>> listMenuCategories(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        User authUser = user == null ? null : user.getUser();
        return callback.execute(() -> categoryService.listCategories(authUser, storeId),
                "Category failed to list",
                null);
    }

    @PatchMapping("/toggle-visibility")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<CategoryResponse> toggleCategoryVisibility(
            @CurrentUser CustomUserDetails user,
            @RequestBody CategoryToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> categoryService.toggleCategoryVisibility(user.getUser(), request),
                "Category failed to hide",
                null);
    }

    @PatchMapping("/toggle-availability")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<CategoryResponse> toggleMenuCategoryAvailability(
            @CurrentUser CustomUserDetails user,
            @RequestBody CategoryToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> categoryService.toggleCategoryAvailability(user.getUser(), request),
                "Category failed to availability",
                null);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<CategoryResponse> deleteMenuCategory(
            @CurrentUser CustomUserDetails user,
            @RequestBody CategoryToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> categoryService.deleteCategory(user.getUser(), request),
                "Category failed to delete",
                null);
    }
}
