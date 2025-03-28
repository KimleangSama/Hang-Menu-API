package com.keakimleang.digital_menu.features.orders.repos;

import com.keakimleang.digital_menu.features.orders.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, UUID> {
    List<OrderMenu> findAllByOrderId(UUID orderId);

    Optional<OrderMenu> findByIdAndOrderId(UUID orderMenuId, UUID orderId);

    void deleteAllByOrderId(UUID orderId);
}
