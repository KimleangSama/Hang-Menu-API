package com.keakimleang.digital_menu.features.notifications.repos;

import com.keakimleang.digital_menu.features.notifications.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    void deleteAllByStoreId(UUID storeId);

    List<Notification> findAllByStoreIdOrderByTimeDesc(UUID storeId);
}
