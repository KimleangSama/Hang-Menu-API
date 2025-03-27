package io.sovann.hang.api.features.stores.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.commons.controllers.*;
import io.sovann.hang.api.commons.payloads.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.payloads.request.*;
import io.sovann.hang.api.features.stores.payloads.request.updates.*;
import io.sovann.hang.api.features.stores.payloads.response.*;
import io.sovann.hang.api.features.stores.services.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.STORE)
@RequiredArgsConstructor
public class StoreController {
    private final StoreServiceImpl storeService;
    private final ControllerServiceCallback callback;
    private final StoreServiceImpl storeServiceImpl;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('admin')")
    public BaseResponse<StoreResponse> createStore(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateStoreRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.createStore(user.getUser(), request),
                "Failed to create store",
                null);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('admin')")
    public BaseResponse<List<StoreResponse>> findAllStoresByUser(
            @CurrentUser CustomUserDetails user
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.findAllStoresByUser(user.getUser()),
                "Failed to get all stores by user",
                null);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('admin')")
    public BaseResponse<StoreResponse> deleteStoreById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.deleteStoreById(user.getUser(), id),
                "Failed to delete store by id",
                null);
    }

    @GetMapping("/{slug}/get")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<StoreResponse> findByStoreSlug(
            @PathVariable String slug
    ) {
        return callback.execute(() -> storeService.findByStoreSlug(slug),
                "Failed to get store by slug",
                null);
    }

    @PatchMapping("/assign-group")
    @PreAuthorize("hasAnyRole('admin')")
    public BaseResponse<List<StoreResponse>> assignStoreToUserGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody AssignGroupRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.assignStoreToUserGroup(user.getUser(), request),
                "Failed to assign store to user group",
                null);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<StoreResponse> updateStoreById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestBody UpdateStoreRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.updateStoreById(user.getUser(), id, request),
                "Failed to update store by id",
                null);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<StoreResponse> findMyStore(
            @CurrentUser CustomUserDetails user
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.findMyStore(user.getUser()),
                "Failed to get my store",
                null);
    }

    @PatchMapping("/{id}/layout")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<StoreResponse> updateStoreLayoutById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestParam String layout
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> {
                    Store store = storeServiceImpl.findStoreEntityById(user.getUser(), id);
                    return storeService.updateStoreLayoutById(user.getUser(), store, layout);
                },
                "Failed to update store layout by id",
                null);
    }
}
