package com.keakimleang.digital_menu.features.orders.repos;

import com.keakimleang.digital_menu.features.orders.entities.*;
import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStoreId(UUID storeId);

    Optional<Order> findByCode(UUID code);

    Integer countOrdersByStoreId(UUID storeId);

    @Query(value = "SELECT SUM(total_amount_in_dollar) FROM orders WHERE store_id = ?1", nativeQuery = true)
    Integer sumTotalUsdByStoreId(UUID storeId);

    @Query(value = "SELECT SUM(total_amount_in_riel) FROM orders WHERE store_id = ?1", nativeQuery = true)
    Integer sumTotalRielByStoreId(UUID storeId);

    @Query(value = "SELECT COUNT(*) FROM orders WHERE store_id = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    Integer countOrdersByStoreIdAndCreatedAtBetween(UUID storeId, LocalDateTime startDateOfLastWeek, LocalDateTime endDateOfLastWeek);

    @Query(value = "SELECT COALESCE(SUM(total_amount_in_dollar), 0) FROM orders WHERE store_id = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    Integer sumTotalUsdByStoreIdAndCreatedAtBetween(UUID storeId, LocalDateTime startDateOfLastWeek, LocalDateTime endDateOfLastWeek);

    @Query(value = "SELECT COALESCE(SUM(total_amount_in_riel), 0) FROM orders WHERE store_id = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    Integer sumTotalRielByStoreIdAndCreatedAtBetween(UUID storeId, LocalDateTime startDateOfLastWeek, LocalDateTime endDateOfLastWeek);

    List<Order> findByStoreIdOrderByCreatedAt(UUID storeId);
}
