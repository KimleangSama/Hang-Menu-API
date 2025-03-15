package io.sovann.hang.api.features.orders.repos;

import io.sovann.hang.api.features.orders.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
