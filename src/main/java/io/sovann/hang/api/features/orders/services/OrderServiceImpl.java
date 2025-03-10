package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.enums.*;
import io.sovann.hang.api.features.orders.payloads.requests.*;
import io.sovann.hang.api.features.orders.payloads.responses.*;
import io.sovann.hang.api.features.orders.repos.*;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.services.*;
import io.sovann.hang.api.features.users.entities.*;
import io.sovann.hang.api.features.users.enums.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl {
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final StoreServiceImpl storeServiceImpl;
    private final OrderMenuRepository orderMenuRepository;

    @Transactional
    @CacheEvict(value = "orders", key = "#request.storeId")
    public OrderQResponse createOrder(CreateOrderRequest request) {
        OrderQResponse response = new OrderQResponse();
        try {
            UUID code = UUID.randomUUID();
            request.setCode(code);
            rabbitTemplate.convertAndSend("order.exchange", "order.created", request);
            log.info("Order request sent to RabbitMQ: {}", request);
            setResponse(response, code, "Order request sent to store.", "200");
        } catch (Exception e) {
            log.error("Failed to send order request to RabbitMQ: {}", e.getMessage());
            setResponse(response, null, "Failed to send order request to store.", "500");
        }
        return response;
    }

    private void setResponse(OrderQResponse response, UUID code, String message, String statusCode) {
        response.setCode(code);
        response.setMessage(message);
        response.setStatusCode(statusCode);
    }

    @Transactional
    @Cacheable(value = "order-entity", key = "#orderId")
    public Optional<Order> getOrderEntityById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional
    @Cacheable(value = "order", key = "#orderId")
    public OrderResponse getOrderById(User user, UUID orderId) {
        return getOrderEntityById(orderId)
                .filter(order -> isUserAuthorizedForStore(user, order.getStore().getId()))
                .map(order -> OrderResponse.fromEntity(order, true))
                .orElse(null);
    }

    @Transactional
    @Cacheable(value = "orders", key = "#storeId")
    public List<OrderResponse> getOrdersByStoreId(User user, UUID storeId) {
        return isUserAuthorizedForStore(user, storeId)
                ? OrderResponse.fromEntities(orderRepository.findByStoreIdOrderByCreatedAt(storeId), false)
                : Collections.emptyList();
    }

    private boolean isUserAuthorizedForStore(User user, UUID storeId) {
        if (user.getRoles().contains(new Role(AuthRole.admin))) {
            return true;
        }
        Store store = storeServiceImpl.getStoreEntityById(user, storeId);
        if (store.getGroup() == null) {
            return false;
        }
        return store.getGroup().getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));
    }

    @Transactional
    @Cacheable(value = "order", key = "#code")
    public OrderResponse getOrderByCode(UUID code) {
        return orderRepository.findByCode(code)
                .map(order -> OrderResponse.fromEntity(order, true))
                .orElse(null);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "order-entity", key = "#orderId"),
            @CacheEvict(value = "order", key = "#orderId"),
            @CacheEvict(value = "orders", allEntries = true),
    })
    public OrderResponse updateOrderStatus(User user, UUID orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> isUserAuthorizedForStore(user, o.getStore().getId()))
                .orElse(null);
        if (order == null) {
            return null;
        }
        order.setStatus(OrderStatus.valueOf(status));
        orderRepository.save(order);
        return OrderResponse.fromEntity(order, false);
    }
}
