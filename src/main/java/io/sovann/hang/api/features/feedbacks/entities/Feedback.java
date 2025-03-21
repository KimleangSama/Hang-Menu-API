package io.sovann.hang.api.features.feedbacks.entities;

import io.sovann.hang.api.features.feedbacks.enums.Rating;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.commons.entities.BaseEntityAudit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;

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