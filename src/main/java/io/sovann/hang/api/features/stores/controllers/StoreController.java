package io.sovann.hang.api.features.stores.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.commons.payloads.BaseResponse;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.stores.payloads.request.AssignGroupRequest;
import io.sovann.hang.api.features.stores.payloads.request.CreateStoreRequest;
import io.sovann.hang.api.features.stores.payloads.request.updates.UpdateStoreRequest;
import io.sovann.hang.api.features.stores.payloads.response.StoreResponse;
import io.sovann.hang.api.features.stores.services.StoreServiceImpl;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(APIURLs.STORE)
@RequiredArgsConstructor
public class StoreController {
    private final StoreServiceImpl storeService;
    private final ControllerServiceCallback callback;
    private final StoreServiceImpl storeServiceImpl;

    @PostMapping("/create")
    public BaseResponse<StoreResponse> createStore(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateStoreRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.createStore(user.getUser(), request),
                "Store failed to create",
                null);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('admin')")
    public BaseResponse<List<StoreResponse>> listStores(
            @CurrentUser CustomUserDetails user
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.list(user.getUser()),
                "Store failed to list",
                null);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('admin')")
    public BaseResponse<StoreResponse> deleteStore(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.deleteStore(user.getUser(), id),
                "Store failed to list",
                null);
    }

    @GetMapping("/{slug}/get")
    public BaseResponse<StoreResponse> getStoreByNameSlug(
            @PathVariable String slug
    ) {
        return callback.execute(() -> storeService.getStoreByNameSlug(slug),
                "Store failed to list",
                null);
    }

    @PatchMapping("/assign-group")
    public BaseResponse<List<StoreResponse>> assignGroup(
            @CurrentUser CustomUserDetails user,
            @RequestBody AssignGroupRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.assignGroup(user.getUser(), request),
                "Store failed to assign group",
                null);
    }

    @PutMapping("/{id}/update")
    public BaseResponse<StoreResponse> updateStore(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestBody UpdateStoreRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.updateStore(user.getUser(), id, request),
                "Store failed to update",
                null);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<StoreResponse> getMyStore(
            @CurrentUser CustomUserDetails user
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.getMyStore(user.getUser()),
                "Store failed to list",
                null);
    }

    @PatchMapping("/{id}/layout")
    public BaseResponse<StoreResponse> updateLayout(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id,
            @RequestParam String layout
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> {
                    Store store = storeServiceImpl.getStoreEntityById(user.getUser(), id);
                    return storeService.updateStoreLayout(user.getUser(), store, layout);
                },
                "Store failed to update layout",
                null);
    }
}
