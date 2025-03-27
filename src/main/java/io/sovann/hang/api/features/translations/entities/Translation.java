package io.sovann.hang.api.features.translations.entities;

import com.redis.om.spring.annotations.*;
import io.sovann.hang.api.features.menus.entities.Menu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@RedisHash("Translation")
@Getter
@Setter
@ToString
@Entity
@Table(name = "translations")
public class Translation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Indexed
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(nullable = false, length = 5)
    private String languageCode;

    private String name;
    private String description;
}