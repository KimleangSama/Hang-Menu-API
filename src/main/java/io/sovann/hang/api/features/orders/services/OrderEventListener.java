package io.sovann.hang.api.features.orders.services;

import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.orders.entities.*;
import io.sovann.hang.api.features.orders.enums.*;
import io.sovann.hang.api.features.orders.payloads.requests.*;
import io.sovann.hang.api.features.orders.repos.*;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.services.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventListener {
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final MenuServiceImpl menuService;
    private final StoreServiceImpl storeServiceImpl;

    @RabbitListener(queues = "order.queue")
    public void handleOrderCreation(CreateOrderRequest request) {
        log.info("Processing order request: {}", request);
        Store store = storeServiceImpl.getStoreEntityById(null, request.getStoreId());
        Order order = new Order();
        order.setStore(store);
        order.setStatus(OrderStatus.pending);
        order.setOrderTime(request.getOrderTime());
        order.setSpecialInstructions(request.getSpecialInstructions());
        Order savedOrder = orderRepository.save(order);

        List<OrderMenu> orderMenus = new ArrayList<>();
        for (CreateOrderMenuRequest orderMenuRequest : request.getOrderMenus()) {
            Menu menu = menuService.getMenuEntityById(orderMenuRequest.getMenuId())
                    .orElse(null);
            if (menu == null) {
                log.error("Menu not found with id: {}", orderMenuRequest.getMenuId());
                continue;
            }
            OrderMenu orderMenu = new OrderMenu();
            orderMenu.setOrder(savedOrder);
            orderMenu.setMenu(menu);
            orderMenu.setQuantity(orderMenuRequest.getQuantity());
            orderMenus.add(orderMenu);
        }
        orderMenuRepository.saveAll(orderMenus);
        log.info("Order successfully processed: {}", savedOrder.getId());
    }
}
