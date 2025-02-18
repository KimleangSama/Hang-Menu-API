package io.sovann.hang.api.features.menus.repos;

import io.sovann.hang.api.features.menus.entities.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuFavoriteRepository extends JpaRepository<Favorite, UUID> {
    Optional<Favorite> findByUserIdAndMenuId(UUID id, UUID menuId);

    List<Favorite> findAllByUserId(UUID userId);
}
