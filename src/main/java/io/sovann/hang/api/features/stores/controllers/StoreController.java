package io.sovann.hang.api.features.stores.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.commons.payloads.PageInfo;
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
            @CurrentUser CustomUserDetails user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        PageInfo meta = new PageInfo(page, size, storeService.count());
        return callback.execute(() -> storeService.listStores(user.getUser(), page, size),
                "Store failed to list",
                meta);
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
                "Store failed to list",
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

    @PatchMapping("/{slug}/layout")
    public BaseResponse<StoreResponse> updateLayout(
            @CurrentUser CustomUserDetails user,
            @PathVariable String slug,
            @RequestParam String layout
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> storeService.updateLayout(user.getUser(), slug, layout),
                "Store failed to list",
                null);
    }
}
