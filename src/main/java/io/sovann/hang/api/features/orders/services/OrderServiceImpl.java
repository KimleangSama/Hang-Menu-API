package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.payloads.requests.*;
import io.sovann.hang.api.features.orders.payloads.responses.*;
import io.sovann.hang.api.features.orders.repos.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl {
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderQResponse createOrder(CreateOrderRequest request) {
        OrderQResponse response = new OrderQResponse();
        try {
            rabbitTemplate.convertAndSend("order.exchange", "order.created", request);
            log.info("Order request sent to RabbitMQ: {}", request);
            setResponse(response, "Order request sent to store.", "200");
        } catch (Exception e) {
            log.error("Failed to send order request to RabbitMQ: {}", e.getMessage());
            setResponse(response, "Failed to send order request to store.", "500");
        }
        return response;
    }

    private void setResponse(OrderQResponse response, String message, String statusCode) {
        response.setMessage(message);
        response.setStatusCode(statusCode);
    }

    @Cacheable(value = "order-entity", key = "#orderId")
    public Optional<Order> getOrderEntityById(UUID orderId) {
        return orderRepository.findById(orderId);
    }
}
