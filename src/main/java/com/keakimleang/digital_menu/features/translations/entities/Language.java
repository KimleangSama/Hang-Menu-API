package com.keakimleang.digital_menu.features.translations.entities;

import com.keakimleang.digital_menu.features.stores.entities.*;
import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Language")
@Getter
@Setter
@ToString
@Entity
@Table(name = "languages")
public class Language implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Indexed
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String code;
    private String name;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
