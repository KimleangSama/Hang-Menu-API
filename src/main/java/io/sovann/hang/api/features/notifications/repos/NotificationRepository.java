package io.sovann.hang.api.features.notifications.repos;

import io.sovann.hang.api.features.notifications.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    void deleteAllByStoreId(UUID storeId);

    List<Notification> findAllByStoreIdOrderByTimeDesc(UUID storeId);
}
