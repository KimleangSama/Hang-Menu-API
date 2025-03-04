package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByStoreIdOrderByPosition(UUID storeId);
}
