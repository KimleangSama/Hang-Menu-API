package io.sovann.hang.api.features.stores.repos;

import io.sovann.hang.api.features.stores.entities.OperatingHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OperatingHourRepository extends JpaRepository<OperatingHour, UUID> {
    @Modifying
    @Query(value = "DELETE FROM operating_hours WHERE store_id = ?1",
            nativeQuery = true)
    void deleteAllByStoreId(UUID id);
}
