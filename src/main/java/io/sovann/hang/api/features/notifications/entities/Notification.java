package io.sovann.hang.api.features.notifications.entities;

import io.sovann.hang.api.features.stores.entities.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@RedisHash("Notifications")
@Getter
@Setter
@ToString
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_type", columnList = "type"),
        @Index(name = "idx_notification_read", columnList = "read"),
        @Index(name = "idx_notification_time", columnList = "time"),
})
public class Notification implements Serializable {
    @Serial
    private final static long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime time = LocalDateTime.now();

    @Column(length = 50)
    private String icon;

    @Column(length = 12)
    private String receiver;

    @Column(nullable = false)
    private boolean read = false;

    @Column(length = 50, nullable = false)
    private String type = "general";

    @Column
    private String link;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
