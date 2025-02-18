package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.MenuImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MenuImageRepository extends JpaRepository<MenuImage, UUID> {
}
