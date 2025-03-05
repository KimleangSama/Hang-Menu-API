package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.*;
import java.time.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findAllByCategory(Category category);

    List<Menu> findAllByCategoryInOrderByCreatedAtDesc(Collection<Category> categories);

    Integer countByCategoryIn(List<Category> categories);

    Integer countByCategoryInAndCreatedAtBetween(Collection<Category> category, LocalDateTime startDateOfLastWeek, LocalDateTime endDateOfLastWeek);
}
