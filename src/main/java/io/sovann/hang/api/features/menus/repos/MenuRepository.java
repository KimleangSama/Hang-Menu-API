package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.entities.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findAllByCategory(Category category);
}
