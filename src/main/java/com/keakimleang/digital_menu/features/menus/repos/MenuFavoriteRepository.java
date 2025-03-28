package com.keakimleang.digital_menu.features.menus.repos;

import com.keakimleang.digital_menu.features.menus.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface MenuFavoriteRepository extends JpaRepository<Favorite, UUID> {
    Optional<Favorite> findByUserIdAndMenuId(UUID id, UUID menuId);

    List<Favorite> findAllByUserId(UUID userId);
}
