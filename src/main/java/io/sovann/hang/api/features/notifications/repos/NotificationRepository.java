package io.sovann.hang.api.features.notifications.repos;

import io.sovann.hang.api.features.notifications.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByStoreId(UUID storeId);
}
