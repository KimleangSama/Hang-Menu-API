package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.menus.entities.MenuImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuImageRepository extends JpaRepository<MenuImage, UUID> {
    void deleteAllByMenu(Menu menu);

    Optional<List<MenuImage>> findByMenuId(UUID id);
}
