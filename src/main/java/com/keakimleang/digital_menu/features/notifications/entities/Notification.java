package com.keakimleang.digital_menu.features.notifications.entities;

import com.keakimleang.digital_menu.features.stores.entities.*;
import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

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

    @Indexed
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
