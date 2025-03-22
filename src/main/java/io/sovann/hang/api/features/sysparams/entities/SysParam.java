package io.sovann.hang.api.features.sysparams.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@RedisHash("SysParams")
@Getter
@Setter
@ToString
@Entity
@Table(name = "sys_params")
public class SysParam implements Serializable {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private Integer maxCategoryNumber;
    private Integer maxMenuNumber;
    private UUID storeId;
}