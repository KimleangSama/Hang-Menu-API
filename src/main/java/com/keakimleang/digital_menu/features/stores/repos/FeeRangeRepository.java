package com.keakimleang.digital_menu.features.stores.repos;

import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface FeeRangeRepository extends JpaRepository<FeeRange, UUID> {
    void deleteAllByOrderingOptionStoreId(UUID id);
}
