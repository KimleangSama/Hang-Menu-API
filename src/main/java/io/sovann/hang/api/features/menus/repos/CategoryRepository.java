package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findAllByStoreId(UUID storeId);
}
