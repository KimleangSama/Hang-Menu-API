package io.sovann.hang.api.features.orders.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.commons.controllers.*;
import io.sovann.hang.api.commons.payloads.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.orders.payloads.requests.*;
import io.sovann.hang.api.features.orders.payloads.responses.*;
import io.sovann.hang.api.features.orders.services.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<OrderResponse> findOrderById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID orderId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> orderService.findOrderById(user.getUser(), orderId),
                "Failed to get order",
                null);
    }

    @GetMapping("/{code}/menus")
    public BaseResponse<OrderResponse> findOrderByCode(
            @PathVariable UUID code
    ) {
        return callback.execute(() -> orderService.findOrderByCode(code),
                "Failed to get order",
                null);
    }

    @GetMapping("/of-store/{storeId}/list")
    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    public BaseResponse<List<OrderResponse>> findAllOrdersByStoreId(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> orderService.findAllOrdersByStoreId(user.getUser(), storeId),
                "Failed to get orders",
                null);
    }

    @PatchMapping("/{orderId}/update")
    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    public BaseResponse<OrderResponse> updateOrderStatusById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID orderId,
            @RequestParam String status
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> orderService.updateOrderStatusById(user.getUser(), orderId, status),
                "Failed to update order status",
                null);
    }
}
