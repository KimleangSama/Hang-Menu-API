package io.sovann.hang.api.features.users.payloads.request;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.users.entities.Group;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

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
