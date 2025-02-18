package io.sovann.hang.api.features.users.payloads.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class PromoteDemoteRequest {
    private UUID groupId;
    private UUID userId;
    private String username;
    private List<UUID> roles;
}
