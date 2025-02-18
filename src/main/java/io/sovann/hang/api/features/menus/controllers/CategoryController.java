package io.sovann.hang.api.features.menus.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.commons.payloads.PageMeta;
import io.sovann.hang.api.features.menus.payloads.requests.CategoryToggleRequest;
import io.sovann.hang.api.features.menus.payloads.requests.CreateCategoryRequest;
import io.sovann.hang.api.features.menus.payloads.responses.CategoryResponse;
import io.sovann.hang.api.features.menus.services.CategoryServiceImpl;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/list")
    public BaseResponse<List<CategoryResponse>> listMenuCategories(
            @CurrentUser CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User authUser = user == null ? null : user.getUser();
        PageMeta pageMeta = new PageMeta(page, size, categoryService.count());
        return callback.execute(() -> categoryService.listCategories(authUser, page, size),
                "Category failed to list",
                pageMeta);
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
