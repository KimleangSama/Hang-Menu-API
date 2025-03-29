package com.keakimleang.digital_menu.features.stores.entities;

import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("OperatingHour")
@Getter
@Setter
@ToString
@Entity
@Table(name = "operating_hours")
public class OperatingHour implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Indexed
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String day;
    private String openTime;
    private String closeTime;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
