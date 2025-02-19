package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.configs.*;
import io.sovann.hang.api.exceptions.*;
import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.payloads.requests.*;
import io.sovann.hang.api.features.orders.payloads.responses.*;
import io.sovann.hang.api.features.orders.repos.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderMenuServiceImpl {
    private final RabbitTemplate rabbitTemplate;
    private final MenuServiceImpl menuService;
    private final OrderMenuRepository orderMenuRepository;
    private final OrderServiceImpl orderServiceImpl;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "order-menus", key = "#request.orderId"),
            @CacheEvict(value = "order-menu", key = "#request.orderId")
    })
    public OrderMenuResponse createOrderMenu(CreateOrderMenuRequest request) {
        try {
            rabbitTemplate.convertAndSend(
                    OrderRabbitMQConfig.QUEUE_NAME, request);
            OrderMenuResponse response = new OrderMenuResponse();
            response.setOrderId(request.getOrderId());
            response.setMenuId(request.getMenuId());
            response.setQuantity(request.getQuantity());
            response.setSpecialRequests(request.getSpecialRequests());
            return response;
        } catch (Exception e) {
            log.error("Error creating order menu", e);
            return null;
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void createOrderMenuListener(CreateOrderMenuRequest request) {
        Order order = orderServiceImpl.getOrderEntityById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId().toString()));
        Menu menu = menuService.getMenuById(request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu", request.getMenuId().toString()));
        OrderMenu orderMenu = new OrderMenu();
        orderMenu.setOrder(order);
        orderMenu.setMenu(menu);
        orderMenu.setQuantity(request.getQuantity());
        orderMenu.setSpecialRequests(request.getSpecialRequests());
        orderMenuRepository.save(orderMenu);
    }

    @Cacheable(value = "order-menus", key = "#orderId")
    public List<OrderMenuResponse> getOrderMenusOfOrder(UUID orderId) {
        List<OrderMenu> orderMenus = orderMenuRepository.findAllByOrderId(orderId);
        return OrderMenuResponse.fromEntities(orderMenus);
    }

    @Caching(evict = {
            @CacheEvict(value = "order-menus", key = "#request.orderId"),
            @CacheEvict(value = "order-menu", key = "#request.orderId")
    })
    public OrderMenuResponse deleteOrderMenu(OrderMenuMutateRequest request) {
        OrderMenu orderMenu = orderMenuRepository.findByIdAndOrderId(request.getOrderMenuId(), request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("OrderMenu and Order", request.getOrderMenuId().toString()
                        + " and " + request.getOrderId().toString()));
        orderMenuRepository.delete(orderMenu);
        return OrderMenuResponse.fromEntity(orderMenu);
    }

    @Cacheable(value = "order-menu", key = "#request.orderId")
    public OrderMenuResponse getOrderMenuById(OrderMenuMutateRequest request) {
        OrderMenu orderMenu = orderMenuRepository.findByIdAndOrderId(request.getOrderMenuId(), request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("orderMenu and order", request.getOrderMenuId().toString()
                        + " and " + request.getOrderId().toString()));
        return OrderMenuResponse.fromEntity(orderMenu);
    }

    @Caching(evict = {
            @CacheEvict(value = "order-menus", key = "#request.orderId"),
            @CacheEvict(value = "order-menu", key = "#request.orderId")
    })
    public OrderMenuResponse updateOrderMenu(OrderMenuMutateRequest request) {
        OrderMenu orderMenu = orderMenuRepository.findByIdAndOrderId(request.getOrderMenuId(), request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("orderMenu and order", request.getOrderMenuId().toString()
                        + " and " + request.getOrderId().toString()));
        orderMenu.setQuantity(request.getQuantity());
        orderMenu.setSpecialRequests(request.getSpecialRequests());
        orderMenuRepository.save(orderMenu);
        return OrderMenuResponse.fromEntity(orderMenu);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "order-menus", key = "#orderId"),
            @CacheEvict(value = "order-menu", key = "#orderId")
    })
    public void deleteOrderMenuOfOrder(UUID orderId) {
        orderMenuRepository.deleteAllByOrderId(orderId);
    }
}
