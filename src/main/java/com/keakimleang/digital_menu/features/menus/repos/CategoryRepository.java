package com.keakimleang.digital_menu.features.menus.repos;

import com.keakimleang.digital_menu.features.menus.entities.*;
import com.keakimleang.digital_menu.features.menus.payloads.responses.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import org.springframework.stereotype.*;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByIdIn(List<UUID> ids);

    List<Category> findAllByStoreIdOrderByPosition(UUID storeId);

    @Query(value = """
            SELECT c.id, c.name, c.description, c.position, COALESCE(COUNT(m.id), 0) AS menu_count
            FROM categories c
            LEFT JOIN menus m ON c.id = m.category_id
            WHERE c.store_id = :storeId
            GROUP BY c.id, c.name, c.position
            ORDER BY c.position
            """, nativeQuery = true)
    List<CategoryResponse> findAllWithMenuCountByStoreId(@Param("storeId") UUID storeId);

    List<Category> findAllByStoreId(UUID storeId);

    Integer countByStore(Store store);

    @Query("SELECT c FROM Category c WHERE c.store.id = :storeId AND c.id = :categoryId")
    CompletableFuture<Optional<Category>> asyncFindById(UUID storeId, UUID categoryId);
}
