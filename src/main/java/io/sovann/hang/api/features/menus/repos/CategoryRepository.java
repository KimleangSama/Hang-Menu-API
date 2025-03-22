package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.payloads.responses.CategoryResponse;
import io.sovann.hang.api.features.stores.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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
}
