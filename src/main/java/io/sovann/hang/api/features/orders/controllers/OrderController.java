package io.sovann.hang.api.features.orders.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.commons.controllers.*;
import io.sovann.hang.api.features.commons.payloads.*;
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
    public BaseResponse<OrderResponse> getOrderById(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID orderId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> orderService.getOrderById(user.getUser(), orderId),
                "Failed to get order",
                null);
    }

    @GetMapping("/of-store/{storeId}/list")
    @PreAuthorize("hasRole('admin') or hasRole('manager')")
    public BaseResponse<List<OrderResponse>> getOrdersByStoreId(
            @CurrentUser CustomUserDetails user,
            @PathVariable UUID storeId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user.getUser());
        return callback.execute(() -> orderService.getOrdersByStoreId(user.getUser(), storeId),
                "Failed to get orders",
                null);
    }
}
