package io.sovann.hang.api.features.commons.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EntityDeletable {
    UUID getDeletedBy();

    LocalDateTime getDeletedAt();
}
