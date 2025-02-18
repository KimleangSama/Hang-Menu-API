package io.sovann.hang.api.features.stores.repos;

import io.sovann.hang.api.features.stores.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LanguageRepository extends JpaRepository<Language, UUID> {
}
