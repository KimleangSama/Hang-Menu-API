package com.keakimleang.digital_menu.features.menus.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.menus.payloads.requests.*;
import com.keakimleang.digital_menu.features.menus.payloads.responses.*;
import com.keakimleang.digital_menu.features.menus.services.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.utils.*;
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
        return callback.execute(() -> categoryService.create(user.user(), request),
                "Category failed to create",
                null);
    }

    @GetMapping("/of-store/{storeId}/list")
    @PreAuthorize("hasAnyRole('admin', 'manager', 'staff', 'cashier')")
    public BaseResponse<List<CategoryResponse>> findAllCategoriesByStoreId(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        User authUser = user == null ? null : user.user();
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
        return callback.execute(() -> categoryService.updateCategoryById(user.user(), id, request),
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
        return callback.execute(() -> categoryService.toggleCategoryVisibility(user.user(), request),
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
        return callback.execute(() -> categoryService.toggleCategoryAvailability(user.user(), request),
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
        return callback.execute(() -> categoryService.deleteCategoryById(user.user(), request),
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
                        user.user(),
                        request.getStoreId(),
                        request.getCategories()
                ),
                "Failed to reorder categories positions",
                null);
    }
}
