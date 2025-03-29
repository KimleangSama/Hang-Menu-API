package com.keakimleang.digital_menu.features.orders.services;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.menus.entities.*;
import com.keakimleang.digital_menu.features.menus.services.*;
import com.keakimleang.digital_menu.features.notifications.payloads.*;
import com.keakimleang.digital_menu.features.orders.entities.*;
import com.keakimleang.digital_menu.features.orders.payloads.requests.*;
import com.keakimleang.digital_menu.features.orders.repos.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.services.*;
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
        Store store = storeServiceImpl.findStoreEntityById(request.getStoreId());

        // Initialize totals
        double totalAmountInRiel = 0;
        double totalAmountInDollar = 0;

        List<OrderMenu> orderMenus = new ArrayList<>();
        // Process order menus first
        for (CreateOrderMenuRequest orderMenuRequest : request.getOrderMenus()) {
            Menu menu = menuService.findMenuEntityById(orderMenuRequest.getMenuId())
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
        if (orderMenus.isEmpty()) {
            log.error("No valid order menus found.");
            return;
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
