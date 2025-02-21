package io.sovann.hang.api.features.stores.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.commons.controllers.*;
import io.sovann.hang.api.features.commons.payloads.*;
import io.sovann.hang.api.features.stores.payloads.request.*;
import io.sovann.hang.api.features.stores.payloads.request.updates.*;
import io.sovann.hang.api.features.stores.payloads.response.*;
import io.sovann.hang.api.features.stores.services.*;
import io.sovann.hang.api.features.users.entities.*;
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

    @PostMapping("/create")
    public BaseResponse<StoreResponse> createStore(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateStoreRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
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
        User currentUser = user.getUser();
        SoftEntityDeletable.throwErrorIfSoftDeleted(currentUser);
        PageMeta meta = new PageMeta(page, size, storeService.count());
        return callback.execute(() -> storeService.listStores(currentUser, page, size),
                "Store failed to list",
                meta);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('admin')")
    public BaseResponse<StoreResponse> deleteStore(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID id
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> storeService.deleteStore(id),
                "Store failed to list",
                null);
    }

//    @GetMapping("/{id}/get")
//    public BaseResponse<StoreResponse> getStore(
//            @CurrentUser CustomUserDetails user,
//            @PathVariable UUID id
//    ) {
//        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
//        return callback.execute(() -> storeService.getStore(user.getUser(), id),
//                "Store failed to list",
//                null);
//    }

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
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
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
}
