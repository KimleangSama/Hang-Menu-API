package io.sovann.hang.api.features.orders.controllers;

import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.orders.payloads.requests.CreateOrderRequest;
import io.sovann.hang.api.features.orders.payloads.responses.OrderResponse;
import io.sovann.hang.api.features.orders.services.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(APIURLs.ORDER)
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceImpl orderService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    public BaseResponse<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request
    ) {
        return callback.execute(() -> orderService.createOrder(request),
                "Order failed to create",
                null);
    }
}
