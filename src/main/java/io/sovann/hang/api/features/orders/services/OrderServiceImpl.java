package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.features.orders.entities.Order;
import io.sovann.hang.api.features.orders.payloads.requests.CreateOrderRequest;
import io.sovann.hang.api.features.orders.payloads.responses.OrderQResponse;
import io.sovann.hang.api.features.orders.payloads.responses.OrderResponse;
import io.sovann.hang.api.features.orders.repos.OrderMenuRepository;
import io.sovann.hang.api.features.orders.repos.OrderRepository;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.stores.services.StoreServiceImpl;
import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl {
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final StoreServiceImpl storeServiceImpl;
    private final OrderMenuRepository orderMenuRepository;

    @Transactional
    @CacheEvict(value = "orders", key = "#request.storeId", allEntries = true)
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
                .map(order -> {
                    return OrderResponse.fromEntity(order, true);
                })
                .orElse(null);
    }

    @Transactional
    @Cacheable(value = "orders", key = "#storeId")
    public List<OrderResponse> getOrdersByStoreId(User user, UUID storeId) {
        return isUserAuthorizedForStore(user, storeId)
                ? OrderResponse.fromEntities(orderRepository.findByStoreId(storeId), false)
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
    @Cacheable(value = "order", key = "#orderId")
    public OrderResponse getOrderById(UUID orderId) {
        return getOrderEntityById(orderId)
                .map(order -> OrderResponse.fromEntity(order, true))
                .orElse(null);
    }
}
