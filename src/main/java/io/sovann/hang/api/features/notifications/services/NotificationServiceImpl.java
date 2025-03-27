package io.sovann.hang.api.features.notifications.services;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.configs.RabbitMQConfig;
import io.sovann.hang.api.constants.CacheValue;
import io.sovann.hang.api.features.notifications.entities.Notification;
import io.sovann.hang.api.features.notifications.payloads.MarkAsReadNotificationRequest;
import io.sovann.hang.api.features.notifications.payloads.NotificationRequest;
import io.sovann.hang.api.features.notifications.payloads.NotificationResponse;
import io.sovann.hang.api.features.notifications.repos.NotificationRepository;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.stores.services.StoreServiceImpl;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.utils.ResourceOwner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl {
    private final NotificationRepository notificationRepository;
    private final StoreServiceImpl storeServiceImpl;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    @CacheEvict(value = CacheValue.NOTIFICATIONS, key = "#request.storeId")
    @Transactional
    public void handleOrderCreation(NotificationRequest request) {
        log.info("Received notification request: {}", request);
        Store store = storeServiceImpl.getStoreEntityById(request.getStoreId());
        var notification = new Notification();
        MMConfig.mapper().map(request, notification);
        notification.setStore(store);
        notificationRepository.save(notification);
    }

    @Transactional
    @Cacheable(value = CacheValue.NOTIFICATIONS, key = "#storeId")
    public List<NotificationResponse> getAllByStoreId(User user, UUID storeId) {
        List<Notification> notifs = notificationRepository.findAllByStoreIdOrderByTimeDesc(storeId);
        return NotificationResponse.fromEntities(notifs);
    }

    @Transactional
    @CacheEvict(value = CacheValue.NOTIFICATIONS, key = "#request.storeId")
    public NotificationResponse markAsRead(User user, MarkAsReadNotificationRequest request) {
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
        if (ResourceOwner.hasPermission(user, storeServiceImpl.getStoreEntityById(storeId))) {
            notificationRepository.deleteAllByStoreId(storeId);
            return null;
        }
        throw new RuntimeException("You don't have permission to delete all notifications of this store");
    }
}
