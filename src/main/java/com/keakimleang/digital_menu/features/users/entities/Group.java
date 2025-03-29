package com.keakimleang.digital_menu.features.users.entities;

import com.keakimleang.digital_menu.commons.entities.*;
import jakarta.persistence.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.data.redis.core.*;

@RedisHash("Group")
@Getter
@Setter
@ToString
@Entity
@Table(name = "groups")
public class Group extends BaseEntityAudit {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<GroupMember> members = new HashSet<>();
}