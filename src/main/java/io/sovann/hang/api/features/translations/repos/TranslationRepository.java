package io.sovann.hang.api.features.translations.repos;

import io.sovann.hang.api.features.translations.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, UUID> {
}
