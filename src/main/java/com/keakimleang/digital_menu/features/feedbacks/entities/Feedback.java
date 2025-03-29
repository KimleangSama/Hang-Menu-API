package com.keakimleang.digital_menu.features.feedbacks.entities;

import com.keakimleang.digital_menu.commons.entities.*;
import com.keakimleang.digital_menu.features.feedbacks.enums.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import jakarta.persistence.*;
import java.io.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Language")
@Getter
@Setter
@ToString
@Entity
@Table(name = "feedbacks")
public class Feedback extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    private String fullname;
    private String phone;
    private String comment;
    @Enumerated(EnumType.STRING)
    private Rating rating;
}