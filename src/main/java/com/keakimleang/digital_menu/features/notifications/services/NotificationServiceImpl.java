package com.keakimleang.digital_menu.features.notifications.services;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.notifications.entities.*;
import com.keakimleang.digital_menu.features.notifications.payloads.*;
import com.keakimleang.digital_menu.features.notifications.repos.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.services.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.utils.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl {
    private final NotificationRepository notificationRepository;
    private final StoreServiceImpl storeServiceImpl;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    @CacheEvict(value = CacheValue.NOTIFICATIONS, key = "#request.storeId")
    public void handleNotificationCreation(NotificationRequest request) {
        log.info("Received notification request: {}", request);
        Store store = storeServiceImpl.findStoreEntityById(request.getStoreId());
        var notification = new Notification();
        MMConfig.mapper().map(request, notification);
        notification.setStore(store);
        notificationRepository.save(notification);
    }

    @Transactional
    @Cacheable(value = CacheValue.NOTIFICATIONS, key = "#storeId")
    public List<NotificationResponse> findAllNotificationsByStoreId(User user, UUID storeId) {
        Store store = storeServiceImpl.findStoreEntityById(storeId);
        if (!ResourceOwner.hasPermission(user, store)) {
            throw new RuntimeException("You don't have permission to view notifications of this store");
        }
        List<Notification> notifs = notificationRepository.findAllByStoreIdOrderByTimeDesc(storeId);
        return NotificationResponse.fromEntities(notifs);
    }

    @Transactional
    @CacheEvict(value = CacheValue.NOTIFICATIONS, key = "#request.storeId")
    public NotificationResponse markAsReadById(User user, MarkAsReadNotificationRequest request) {
        Notification notification = notificationRepository.findById(request.getNotificationId())
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (ResourceOwner.hasPermission(user, notification.getStore())) {
            notification.setRead(true);
            notificationRepository.save(notification);
            return NotificationResponse.fromEntity(notification);
        }
        throw new RuntimeException("You don't have permission to mark this notification as read");
    }

    @Transactional
    @CacheEvict(value = CacheValue.NOTIFICATIONS, key = "#storeId")
    public Void deleteAllByStoreId(User user, UUID storeId) {
        if (ResourceOwner.hasPermission(user, storeServiceImpl.findStoreEntityById(storeId))) {
            notificationRepository.deleteAllByStoreId(storeId);
            return null;
        }
        throw new RuntimeException("You don't have permission to delete all notifications of this store");
    }
}
