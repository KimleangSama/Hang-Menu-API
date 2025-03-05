package io.sovann.hang.api.features.users.entities;

import jakarta.persistence.*;
import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.hibernate.annotations.*;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntityAudit extends BaseEntity implements Serializable {
    @Column(name = "created_by", updatable = false)
    private UUID createdBy;
    @Column(name = "updated_by")
    private UUID updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntityAudit that)) return false;
        if (!super.equals(o)) return false;
        return createdBy.equals(that.createdBy) &&
                updatedBy.equals(that.updatedBy) &&
                createdAt.equals(that.createdAt) &&
                updatedAt.equals(that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(),
                createdBy, updatedBy, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "BaseEntityAudit{" + "createdBy='" + createdBy + ", updatedBy='" +
                updatedBy + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt +
                "}" + super.toString();
    }
}
