package com.keakimleang.digital_menu.features.orders.services;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.orders.entities.*;
import com.keakimleang.digital_menu.features.orders.enums.*;
import com.keakimleang.digital_menu.features.orders.payloads.requests.*;
import com.keakimleang.digital_menu.features.orders.payloads.responses.*;
import com.keakimleang.digital_menu.features.orders.repos.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.services.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.enums.*;
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

    @Transactional
    @CacheEvict(value = "orders", key = "#request.storeId")
    public OrderQResponse createOrder(CreateOrderRequest request) {
        Store store = storeServiceImpl.findStoreEntityById(request.getStoreId());
        if (store == null) {
            log.error("Store not found with id: {}", request.getStoreId());
            return null;
        }
        OrderQResponse response = new OrderQResponse();
        try {
            UUID code = UUID.randomUUID();
            request.setCode(code);
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, request);
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
    public OrderResponse findOrderById(User user, UUID orderId) {
        return getOrderEntityById(orderId)
                .filter(order -> isUserAuthorizedForStore(user, order.getStore().getId()))
                .map(order -> OrderResponse.fromEntity(order, true))
                .orElse(null);
    }

    @Transactional
    @Cacheable(value = "orders", key = "#storeId")
    public List<OrderResponse> findAllOrdersByStoreId(User user, UUID storeId) {
        return isUserAuthorizedForStore(user, storeId)
                ? OrderResponse.fromEntities(orderRepository.findByStoreIdOrderByCreatedAt(storeId), false)
                : Collections.emptyList();
    }

    private boolean isUserAuthorizedForStore(User user, UUID storeId) {
        if (user.getRoles().contains(new Role(AuthRole.admin))) {
            return true;
        }
        Store store = storeServiceImpl.findStoreEntityById(user, storeId);
        if (store.getGroup() == null) {
            return false;
        }
        return store.getGroup().getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));
    }

    @Transactional
    @Cacheable(value = "order", key = "#code")
    public OrderResponse findOrderByCode(UUID code) {
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
    public OrderResponse updateOrderStatusById(User user, UUID orderId, String status) {
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
