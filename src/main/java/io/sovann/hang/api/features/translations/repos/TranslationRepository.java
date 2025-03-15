package io.sovann.hang.api.features.translations.repos;

import io.sovann.hang.api.features.translations.entities.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, UUID> {
}
