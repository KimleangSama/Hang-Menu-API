package io.sovann.hang.api.features.menus.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@RedisHash("MenuImage")
@Getter
@Setter
@ToString
@Entity
@Table(name = "menu_images", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"url"})
})
public class MenuImage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String name;
    private String url;

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu;
}
