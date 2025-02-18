package io.sovann.hang.api.features.stores.payloads.response;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.stores.entities.Store;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import java.util.UUID;

@Getter
@Setter
@ToString
public class StoreResponse {
    private UUID id;
    private String name;
    private String slug;
    private String logo;
    private String color;
    private String description;
    private String physicalAddress;
    private String virtualAddress;
    private String phone;
    private String email;
    private String website;
    private String facebook;
    private String telegram;
    private String instagram;
    private String cover;
    private String banner;
    private String layout;

    private UUID createdBy;
    private boolean hasPrivilege = false;

    private StoreInfoResponse storeInfoResponse;

    public static StoreResponse fromEntity(Store store) {
        ModelMapper mm = MMConfig.mapper();
        StoreResponse response = mm.map(store, StoreResponse.class);
        response.setStoreInfoResponse(StoreInfoResponse.fromEntity(store));
        return response;
    }
}
