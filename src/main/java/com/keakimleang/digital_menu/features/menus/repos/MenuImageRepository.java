package com.keakimleang.digital_menu.features.menus.repos;

import com.keakimleang.digital_menu.features.menus.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface MenuImageRepository extends JpaRepository<MenuImage, UUID> {
    void deleteAllByMenu(Menu menu);

    Optional<List<MenuImage>> findByMenuId(UUID id);
}
