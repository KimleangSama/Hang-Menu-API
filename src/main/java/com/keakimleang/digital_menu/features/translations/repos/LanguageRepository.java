package com.keakimleang.digital_menu.features.translations.repos;

import com.keakimleang.digital_menu.features.translations.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface LanguageRepository extends JpaRepository<Language, UUID> {
    List<Language> findAllByStoreId(UUID storeId);

    boolean existsByCode(String code);
}
