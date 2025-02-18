package io.sovann.hang.api.features.carts.entities;

import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.users.entities.BaseEntityAudit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.util.List;

@RedisHash("Cart")
@Getter
@Setter
@ToString
@Entity
@Table(name = "carts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"created_by"})
})
public class Cart extends BaseEntityAudit {
    @Serial
    private final static long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "cart")
    private List<CartMenu> cartMenus;
}
