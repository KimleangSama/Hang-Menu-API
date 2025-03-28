package com.keakimleang.digital_menu.features.users.payloads.request;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import lombok.*;
import org.modelmapper.*;

@Getter
@Setter
@ToString
public class CreateGroupRequest {
    private String name;
    private String description;

    public static Group fromRequest(CreateGroupRequest request) {
        ModelMapper mm = MMConfig.mapper();
        return mm.map(request, Group.class);
    }
}
