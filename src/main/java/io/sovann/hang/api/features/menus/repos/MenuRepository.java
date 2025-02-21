package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface MenuRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findAllByCategory(Category category);

    List<Menu> findAllByCategoryIn(Collection<Category> categories);
}
