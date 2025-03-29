package com.keakimleang.digital_menu.features.orders.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.orders.payloads.requests.*;
import com.keakimleang.digital_menu.features.orders.payloads.responses.*;
import com.keakimleang.digital_menu.features.orders.services.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.utils.*;
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
        return callback.execute(() -> orderService.findOrderById(user.user(), orderId),
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
        return callback.execute(() -> orderService.findAllOrdersByStoreId(user.user(), storeId),
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
        return callback.execute(() -> orderService.updateOrderStatusById(user.user(), orderId, status),
                "Failed to update order status",
                null);
    }
}
