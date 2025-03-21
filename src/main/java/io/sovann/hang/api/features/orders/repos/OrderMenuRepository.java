package io.sovann.hang.api.features.orders.repos;

import io.sovann.hang.api.features.orders.entities.OrderMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, UUID> {
    List<OrderMenu> findAllByOrderId(UUID orderId);

    Optional<OrderMenu> findByIdAndOrderId(UUID orderMenuId, UUID orderId);

    void deleteAllByOrderId(UUID orderId);
}
