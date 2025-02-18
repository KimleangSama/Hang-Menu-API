package io.sovann.hang.api.features.users.payloads.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class AddOrRemoveGroupMemberRequest {
    private UUID groupId;
    private UUID userId;
    private String username;
}
