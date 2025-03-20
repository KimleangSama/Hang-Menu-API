package io.sovann.hang.api.commons.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EntityDeletable {
    UUID getDeletedBy();

    LocalDateTime getDeletedAt();
}
