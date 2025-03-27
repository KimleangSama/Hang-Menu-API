package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.configs.RabbitMQConfig;
import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.menus.services.MenuServiceImpl;
import io.sovann.hang.api.features.notifications.payloads.NotificationRequest;
import io.sovann.hang.api.features.orders.entities.Order;
import io.sovann.hang.api.features.orders.entities.OrderMenu;
import io.sovann.hang.api.features.orders.payloads.requests.CreateOrderMenuRequest;
import io.sovann.hang.api.features.orders.payloads.requests.CreateOrderRequest;
import io.sovann.hang.api.features.orders.repos.OrderMenuRepository;
import io.sovann.hang.api.features.orders.repos.OrderRepository;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.stores.services.StoreServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @RabbitListener(queues = "order.queue")
    public void handleOrderCreation(CreateOrderRequest request) {
        Store store = storeServiceImpl.getStoreEntityById(request.getStoreId());
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
