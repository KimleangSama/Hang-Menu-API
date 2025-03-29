package com.keakimleang.digital_menu.features.stores.entities;

import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("FeeRange")
@Getter
@Setter
@ToString
@Entity
@Table(name = "fee_ranges")
public class FeeRange implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Indexed
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private String condition;
    private Double fee;

    @ManyToOne
    @JoinColumn(name = "ordering_option_id")
    private OrderingOption orderingOption;
}
