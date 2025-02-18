package io.sovann.hang.api.features.stores.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@RedisHash("Language")
@Getter
@Setter
@ToString
@Entity
@Table(name = "languages")
public class Language implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String language;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
