package com.keakimleang.digital_menu.features.stores.entities;

import com.keakimleang.digital_menu.commons.entities.*;
import com.keakimleang.digital_menu.features.translations.entities.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Store")
@Getter
@Setter
@ToString
@Entity
@Table(name = "stores", indexes = {
        @Index(name = "idx_store_name", columnList = "name"),
        @Index(name = "idx_store_slug", columnList = "slug"),
})
public class Store extends BaseEntityAudit {
    @Serial
    private final static long serialVersionUID = 1L;

    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String slug;
    private String logo;
    private String color = "#D22530";
    private String description;
    private String physicalAddress;
    private String virtualAddress;
    private String phone;
    private String email;
    private String website;
    private String facebook;
    private String telegram;
    private String instagram;
    private String promotion;
    private String banner;
    private String layout;
    private Double lat;
    private Double lng;
    private Boolean showGoogleMap = true;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperatingHour> operatingHours;
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderingOption> orderingOptions;
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentMethod> paymentMethods;
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Language> languages;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", unique = true, nullable = false)
    private Group group;
}
