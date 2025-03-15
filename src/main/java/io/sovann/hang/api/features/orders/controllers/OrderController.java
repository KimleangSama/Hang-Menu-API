package io.sovann.hang.api.features.orders.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.orders.payloads.requests.CreateOrderRequest;
import io.sovann.hang.api.features.orders.payloads.responses.OrderQResponse;
import io.sovann.hang.api.features.orders.payloads.responses.OrderResponse;
import io.sovann.hang.api.features.orders.services.OrderServiceImpl;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(APIURLs.ORDER)
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    public BaseResponse<OrderQResponse> createOrder(
            @RequestBody CreateOrderRequest request
    ) {
        return callback.execute(() -> orderService.createOrder(request),
                "Order failed to create",
                null);
    }

    @GetMapping("/{orderId}/details")
    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    public BaseResponse<OrderResponse> getOrderById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID orderId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> orderService.getOrderById(user.getUser(), orderId),
                "Failed to get order",
                null);
    }

    @GetMapping("/{code}/menus")
    public BaseResponse<OrderResponse> getOrderByCode(
            @PathVariable UUID code
    ) {
        return callback.execute(() -> orderService.getOrderByCode(code),
                "Failed to get order",
                null);
    }

    @GetMapping("/of-store/{storeId}/list")
    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    public BaseResponse<List<OrderResponse>> getOrdersByStoreId(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> orderService.getOrdersByStoreId(user.getUser(), storeId),
                "Failed to get orders",
                null);
    }

    @PatchMapping("/{orderId}/update")
    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    public BaseResponse<OrderResponse> updateOrderStatus(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID orderId,
            @RequestParam String status
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> orderService.updateOrderStatus(user.getUser(), orderId, status),
                "Failed to update order status",
                null);
    }
}
