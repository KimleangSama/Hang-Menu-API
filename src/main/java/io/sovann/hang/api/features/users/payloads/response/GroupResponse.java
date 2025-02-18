package io.sovann.hang.api.features.users.payloads.response;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.users.entities.Group;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.UUID;

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
