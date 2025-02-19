package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.exceptions.*;
import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.enums.*;
import io.sovann.hang.api.features.orders.payloads.requests.*;
import io.sovann.hang.api.features.orders.payloads.responses.*;
import io.sovann.hang.api.features.orders.repos.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl {
    private final MenuServiceImpl menuService;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;

    private double calculateTotalAmount(List<OrderMenu> orderMenus) {
        return orderMenus.stream()
                .mapToDouble(orderMenu -> orderMenu.getQuantity() * orderMenu.getMenu().getPrice())
                .sum();
    }

    public OrderResponse createOrder(CreateOrderRequest request) {
        List<CreateOrderMenuRequest> orderMenusRequest = extractOrderMenuRequests(request.getOrderMenus());
        if (orderMenusRequest.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order without menu.");
        }
        List<OrderMenu> orderMenus = createOrderMenus(orderMenusRequest);
        if (orderMenus.isEmpty()) {
            throw new IllegalArgumentException("No valid menu found to create an order.");
        }
        double totalAmount = calculateTotalAmount(orderMenus);
        Order order = new Order();
        order.setStatus(OrderStatus.pending);
        order.setOrderTime(request.getOrderTime());
        order.setTotalAmount(totalAmount);
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setOrderMenus(orderMenus);
        Order savedOrder = orderRepository.save(order);
        orderMenus.forEach(orderMenu -> orderMenu.setOrder(savedOrder));
        orderMenuRepository.saveAll(orderMenus);
        return OrderResponse.fromEntity(savedOrder);
    }

    private List<CreateOrderMenuRequest> extractOrderMenuRequests(List<CreateOrderMenuRequest> orderMenus) {
        return orderMenus.stream()
                .filter(orderMenu -> orderMenu.getMenuId() != null)
                .toList();
    }

    private List<OrderMenu> createOrderMenus(List<CreateOrderMenuRequest> requests) {
        return requests.stream()
                .map(this::createOrderMenu)
                .filter(Optional::isPresent) // Filter out invalid items
                .map(Optional::get)
                .toList();
    }

    private Optional<OrderMenu> createOrderMenu(CreateOrderMenuRequest request) {
        try {
            Menu menu = menuService.getMenuById(request.getMenuId())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu", request.getMenuId().toString()));
            return Optional.of(new OrderMenu(menu, request.getQuantity(), request.getSpecialRequests()));
        } catch (ResourceNotFoundException e) {
            log.warn("Skipping unavailable menu item: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error processing order menu: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Cacheable(value = "order-entity", key = "#orderId")
    public Optional<Order> getOrderEntityById(UUID orderId) {
        return orderRepository.findById(orderId);
    }
}
