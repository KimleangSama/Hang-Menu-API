package io.sovann.hang.api.features.notifications.services;

import io.sovann.hang.api.configs.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.notifications.entities.*;
import io.sovann.hang.api.features.notifications.payloads.*;
import io.sovann.hang.api.features.notifications.repos.*;
import io.sovann.hang.api.features.users.entities.*;
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

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    @Transactional
    public void handleOrderCreation(NotificationRequest notificationRequest) {
        log.info("Received notification request: {}", notificationRequest);
        var notification = new Notification();
        notification.setMessage(notificationRequest.getMessage());
        notification.setTime(notificationRequest.getTime());
        notification.setIcon(notificationRequest.getIcon());
        notification.setReceiver(notificationRequest.getReceiver());
        notification.setRead(notificationRequest.isRead());
        notification.setType(notificationRequest.getType());
        notification.setLink(notificationRequest.getLink());
        notificationRepository.save(notification);
    }

    @Transactional
    @Cacheable(value = CacheValue.NOTIFICATIONS, key = "#storeId")
    public List<NotificationResponse> getAllByStoreId(User user, UUID storeId) {
        List<Notification> notifs = notificationRepository.findAllByStoreId(storeId);
        return NotificationResponse.fromEntities(notifs);
    }
}
