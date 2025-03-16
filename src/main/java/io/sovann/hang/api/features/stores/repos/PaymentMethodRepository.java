package io.sovann.hang.api.features.stores.repos;

import io.sovann.hang.api.features.stores.entities.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    @Modifying
    @Query(value = "DELETE FROM payment_methods WHERE store_id = ?1",
            nativeQuery = true)
    void deleteAllByStoreId(UUID id);
}
