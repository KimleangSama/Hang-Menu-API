package io.sovann.hang.api.features.menus.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.commons.controllers.*;
import io.sovann.hang.api.commons.payloads.*;
import io.sovann.hang.api.constants.*;
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
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> categoryService.create(user.getUser(), request),
                "Category failed to create",
                null);
    }

    @GetMapping("/of-store/{storeId}/list")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<List<CategoryResponse>> findAllCategoriesByStoreId(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        User authUser = user == null ? null : user.getUser();
        return callback.execute(() -> categoryService.findAllCategoriesByStoreId(authUser, storeId),
                "Category failed to list",
                null);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<CategoryResponse> updateCategoryById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestBody UpdateCategoryRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> categoryService.updateCategoryById(user.getUser(), id, request),
                "Category failed to update",
                null);
    }

    @PatchMapping("/toggle-visibility")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<CategoryResponse> toggleCategoryVisibility(
            @CurrentUser CustomUserDetails user,
            @RequestBody CategoryToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> categoryService.toggleCategoryVisibility(user.getUser(), request),
                "Failed to toggle category visibility",
                null);
    }

    @PatchMapping("/toggle-availability")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<CategoryResponse> toggleCategoryAvailability(
            @CurrentUser CustomUserDetails user,
            @RequestBody CategoryToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> categoryService.toggleCategoryAvailability(user.getUser(), request),
                "Failed to toggle category availability",
                null);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<CategoryResponse> deleteCategoryById(
            @CurrentUser CustomUserDetails user,
            @RequestBody CategoryToggleRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> categoryService.deleteCategoryById(user.getUser(), request),
                "Category failed to delete",
                null);
    }

    @PostMapping("/reorder")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<List<CategoryResponse>> orderCategoriesPositions(
            @CurrentUser CustomUserDetails user,
            @RequestBody CategoryReorderRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> categoryService.orderCategoriesPositions(
                        user.getUser(),
                        request.getStoreId(),
                        request.getCategories()
                ),
                "Failed to reorder categories positions",
                null);
    }
}
