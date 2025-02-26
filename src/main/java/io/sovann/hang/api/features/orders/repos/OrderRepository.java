package io.sovann.hang.api.features.orders.repos;

import io.sovann.hang.api.features.orders.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStoreId(UUID storeId);

    Optional<Order> findByCode(UUID code);
}
