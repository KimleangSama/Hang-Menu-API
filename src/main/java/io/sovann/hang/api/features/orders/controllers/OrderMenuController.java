package io.sovann.hang.api.features.orders.controllers;

import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.commons.controllers.*;
import io.sovann.hang.api.features.commons.payloads.*;
import io.sovann.hang.api.features.orders.payloads.requests.*;
import io.sovann.hang.api.features.orders.payloads.responses.*;
import io.sovann.hang.api.features.orders.services.*;
import java.util.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.ORDER_MENU)
@RequiredArgsConstructor
public class OrderMenuController {
    private final OrderMenuServiceImpl orderMenuServiceImpl;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    public BaseResponse<OrderMenuResponse> createOrderMenu(
            @RequestBody CreateOrderMenuRequest request
    ) {
        return callback.execute(() -> orderMenuServiceImpl.createOrderMenu(request),
                "OrderMenu failed to create",
                null);
    }

    @GetMapping("/of-order/{orderId}/list")
    public BaseResponse<List<OrderMenuResponse>> getOrderMenusOfOrder(
            @PathVariable UUID orderId
    ) {
        return callback.execute(() -> orderMenuServiceImpl.getOrderMenusOfOrder(orderId),
                "OrderMenu failed to get",
                null);
    }

    @DeleteMapping("/delete")
    public BaseResponse<OrderMenuResponse> deleteOrderMenu(
            @RequestBody OrderMenuMutateRequest request
    ) {
        return callback.execute(() -> orderMenuServiceImpl.deleteOrderMenu(request),
                "OrderMenu failed to delete",
                null);
    }

    @PostMapping("/get")
    public BaseResponse<OrderMenuResponse> getOrderMenuById(
            @RequestBody OrderMenuMutateRequest request
    ) {
        return callback.execute(() -> orderMenuServiceImpl.getOrderMenuById(request),
                "Cart failed to get",
                null);
    }

    @PatchMapping("/update")
    public BaseResponse<OrderMenuResponse> updateOrderMenu(
            @RequestBody OrderMenuMutateRequest request
    ) {
        return callback.execute(() -> orderMenuServiceImpl.updateOrderMenu(request),
                "OrderMenu failed to update",
                null);
    }
}
