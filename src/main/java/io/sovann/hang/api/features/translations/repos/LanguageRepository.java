package io.sovann.hang.api.features.translations.repos;

import io.sovann.hang.api.features.translations.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LanguageRepository extends JpaRepository<Language, UUID> {
    List<Language> findAllByStoreId(UUID storeId);
}
