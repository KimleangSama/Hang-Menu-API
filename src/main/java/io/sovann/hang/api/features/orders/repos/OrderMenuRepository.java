package io.sovann.hang.api.features.orders.repos;

import io.sovann.hang.api.features.orders.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, UUID> {
    List<OrderMenu> findAllByOrderId(UUID orderId);

    Optional<OrderMenu> findByIdAndOrderId(UUID orderMenuId, UUID orderId);

    void deleteAllByOrderId(UUID orderId);
}
