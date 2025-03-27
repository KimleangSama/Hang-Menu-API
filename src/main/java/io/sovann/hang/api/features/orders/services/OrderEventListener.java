package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.configs.*;
import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.notifications.payloads.*;
import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.payloads.requests.*;
import io.sovann.hang.api.features.orders.repos.*;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.services.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.*;
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
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderCreation(CreateOrderRequest request) {
        log.info("Handling order creation");
        Store store = storeServiceImpl.findStoreEntityById(request.getStoreId());
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
            MMConfig.mapper().map(menu, orderMenu);
            orderMenu.setMenuId(menu.getId());
            orderMenu.setQuantity(orderMenuRequest.getQuantity());
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

        // Send notification to store owner
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setMessage("New order received for store: " + store.getName());
        notificationRequest.setTime(LocalDateTime.now());
        notificationRequest.setIcon("order");
        notificationRequest.setReceiver(order.getPhoneNumber());
        notificationRequest.setRead(false);
        notificationRequest.setType("order");
        notificationRequest.setLink("/orders/" + order.getId());
        notificationRequest.setStoreId(store.getId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                notificationRequest
        );
    }
}
