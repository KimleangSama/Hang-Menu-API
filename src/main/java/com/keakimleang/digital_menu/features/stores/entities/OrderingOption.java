package com.keakimleang.digital_menu.features.stores.entities;

import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("OrderingOption")
@Getter
@Setter
@ToString
@Entity
@Table(name = "ordering_options")
public class OrderingOption implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Indexed
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "orderingOption", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FeeRange> feeRanges;
}
