package io.sovann.hang.api.features.stores.payloads.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class AssignGroupRequest {
    private UUID groupId;
    private List<UUID> storeIds;
}
