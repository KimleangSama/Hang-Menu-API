package io.sovann.hang.api.features.stores.repos;

import io.sovann.hang.api.features.stores.entities.OrderingOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderingOptionRepository extends JpaRepository<OrderingOption, UUID> {
    @Modifying
    @Query(value = "DELETE FROM ordering_options WHERE store_id = ?1",
            nativeQuery = true)
    void deleteAllByStoreId(UUID id);
}
