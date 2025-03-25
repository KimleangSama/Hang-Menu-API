package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.Category;
import io.sovann.hang.api.features.menus.entities.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findAllByCategory(Category category);

    List<Menu> findAllByCategoryInOrderByCreatedAtDesc(Collection<Category> categories);

    Integer countByCategoryIn(List<Category> categories);

    Integer countByCategoryInAndCreatedAtBetween(Collection<Category> category, LocalDateTime startDateOfLastWeek, LocalDateTime endDateOfLastWeek);

    Integer countByCategory(Category category);

    @Query("SELECT COUNT(m) FROM Menu m WHERE m.category = ?1")
    CompletableFuture<Integer> asyncCountByCategory(Category category);
}
