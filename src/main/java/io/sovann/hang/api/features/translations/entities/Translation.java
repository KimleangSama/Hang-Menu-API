package io.sovann.hang.api.features.translations.entities;

import io.sovann.hang.api.features.menus.entities.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Translation")
@Getter
@Setter
@ToString
@Entity
@Table(name = "translations")
public class Translation implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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