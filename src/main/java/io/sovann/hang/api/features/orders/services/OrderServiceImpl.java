package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.carts.services.CartMenuServiceImpl;
import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.menus.services.MenuServiceImpl;
import io.sovann.hang.api.features.orders.entities.Order;
import io.sovann.hang.api.features.orders.entities.OrderMenu;
import io.sovann.hang.api.features.orders.payloads.requests.CreateOrderMenuRequest;
import io.sovann.hang.api.features.orders.payloads.requests.CreateOrderRequest;
import io.sovann.hang.api.features.orders.payloads.responses.OrderResponse;
import io.sovann.hang.api.features.orders.repos.OrderMenuRepository;
import io.sovann.hang.api.features.orders.repos.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl {
    private final MenuServiceImpl menuService;
    private final CartMenuServiceImpl CartMenuService;
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;

    public OrderResponse createOrder(CreateOrderRequest request) {
        List<CreateOrderMenuRequest> orderMenusRequest = extractOrderMenuRequests(request.getCartId());
        if (orderMenusRequest.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order without menu.");
        }
        List<OrderMenu> orderMenus = createOrderMenus(orderMenusRequest);
        if (orderMenus.isEmpty()) {
            throw new IllegalArgumentException("No valid menu found to create an order.");
        }
        double totalAmount = calculateTotalAmount(orderMenus);
        Order order = new Order();
        order.setStatus(request.getStatus());
        order.setOrderTime(request.getOrderTime());
        order.setTotalAmount(totalAmount);
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setOrderMenus(orderMenus);
        Order savedOrder = orderRepository.save(order);
        orderMenus.forEach(orderMenu -> orderMenu.setOrder(savedOrder));
        List<OrderMenu> savedMenus = orderMenuRepository.saveAll(orderMenus);
        if (!savedMenus.isEmpty()) {
            CartMenuService.deleteCartMenuOfCart(request.getCartId());
        }
        return OrderResponse.fromEntity(savedOrder);
    }

    private List<CreateOrderMenuRequest> extractOrderMenuRequests(UUID cartId) {
        return CartMenuService.getCartMenuOfCart(cartId).stream()
                .map(CartMenu -> CreateOrderMenuRequest.builder()
                        .menuId(CartMenu.getMenuId())
                        .quantity(CartMenu.getQuantity())
                        .specialRequests(CartMenu.getSpecialRequests())
                        .build())
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

            return Optional.of(new OrderMenu(menu, request.getQuantity(), menu.getPrice(), request.getSpecialRequests()));
        } catch (ResourceNotFoundException e) {
            log.warn("Skipping unavailable menu item: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error processing order menu: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private double calculateTotalAmount(List<OrderMenu> orderMenus) {
        return orderMenus.stream()
                .mapToDouble(orderMenu -> orderMenu.getQuantity() * orderMenu.getUnitPrice())
                .sum();
    }
}
