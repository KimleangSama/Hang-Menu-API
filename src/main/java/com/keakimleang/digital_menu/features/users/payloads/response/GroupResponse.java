package com.keakimleang.digital_menu.features.users.payloads.response;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import java.util.*;
import lombok.*;
import org.modelmapper.*;

@Getter
@Setter
@ToString
public class GroupResponse {
    private UUID id;
    private String name;
    private String description;

    public static GroupResponse fromEntity(Group group) {
        ModelMapper mm = MMConfig.mapper();
        return mm.map(group, GroupResponse.class);
    }
}
