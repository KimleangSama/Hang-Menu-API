package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.payloads.requests.*;
import io.sovann.hang.api.features.orders.repos.*;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.services.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final MenuServiceImpl menuService;
    private final StoreServiceImpl storeServiceImpl;

    @Transactional
    @RabbitListener(queues = "order.queue")
    public void handleOrderCreation(CreateOrderRequest request) {
        Store store = storeServiceImpl.getStoreEntityById(null, request.getStoreId());

        // Initialize totals
        double totalAmountInRiel = 0;
        double totalAmountInDollar = 0;

        List<OrderMenu> orderMenus = new ArrayList<>();
        // Process order menus first
        for (CreateOrderMenuRequest orderMenuRequest : request.getOrderMenus()) {
            Menu menu = menuService.getMenuEntityById(orderMenuRequest.getMenuId())
                    .orElse(null);
            if (menu == null) {
                log.error("Menu not found with id: {}", orderMenuRequest.getMenuId());
                continue;
            }
            OrderMenu orderMenu = new OrderMenu();
            orderMenu.setMenuId(menu.getId());
            orderMenu.setCode(menu.getCode());
            orderMenu.setName(menu.getName());
            orderMenu.setImage(menu.getImage());
            orderMenu.setDescription(menu.getDescription());
            orderMenu.setQuantity(orderMenuRequest.getQuantity());
            orderMenu.setPrice(menu.getPrice());
            orderMenu.setCurrency(menu.getCurrency());
            orderMenu.setDiscount(menu.getDiscount());
            // Calculate total amount
            if ("riel".equalsIgnoreCase(menu.getCurrency())) {
                totalAmountInRiel += menu.getPrice() * orderMenuRequest.getQuantity();
            } else {
                totalAmountInDollar += menu.getPrice() * orderMenuRequest.getQuantity();
            }
            orderMenus.add(orderMenu);
        }
        // Create order after all calculations
        Order order = new Order();
        order.setCode(request.getCode());
        order.setStore(store);
        order.setPhoneNumber(request.getPhoneNumber());
        order.setStatus(request.getStatus());
        order.setOrderTime(request.getOrderTime());
        order.setSpecialInstructions(request.getSpecialInstructions());
        order.setTotalAmountInRiel(totalAmountInRiel);
        order.setTotalAmountInDollar(totalAmountInDollar);
        // Save order
        Order savedOrder = orderRepository.saveAndFlush(order);
        // Associate order with orderMenus
        for (OrderMenu orderMenu : orderMenus) {
            orderMenu.setOrder(savedOrder);
        }
        // Save order menus
        orderMenuRepository.saveAll(orderMenus);
    }
}
