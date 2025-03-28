package com.keakimleang.digital_menu.features.menus.repos;

import com.keakimleang.digital_menu.features.menus.entities.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

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
