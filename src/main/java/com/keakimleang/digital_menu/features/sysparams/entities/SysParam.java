package com.keakimleang.digital_menu.features.sysparams.entities;

import com.redis.om.spring.annotations.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("SysParams")
@Getter
@Setter
@ToString
@Entity
@Table(name = "sys_params")
public class SysParam implements Serializable {
    @Indexed
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private Integer maxCategoryNumber;
    private Integer maxMenuNumber;
    private UUID storeId;
}