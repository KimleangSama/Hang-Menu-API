package com.keakimleang.digital_menu.features.stores.repos;

import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface OrderingOptionRepository extends JpaRepository<OrderingOption, UUID> {
    @Modifying
    @Query(value = "DELETE FROM ordering_options WHERE store_id = ?1",
            nativeQuery = true)
    void deleteAllByStoreId(UUID id);
}
