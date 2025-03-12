package io.sovann.hang.api.features.stores.payloads.response;

import io.sovann.hang.api.configs.*;
import io.sovann.hang.api.features.stores.entities.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.modelmapper.*;

@Getter
@Setter
@ToString
public class StoreResponse implements Serializable {
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
    private String promotion;
    private String banner;
    private String layout;

    private UUID createdBy;
    private boolean hasPrivilege = false;
    private UUID groupId;

    private StoreInfoResponse storeInfoResponse;

    public static StoreResponse fromEntity(Store store) {
        ModelMapper mm = MMConfig.mapper();
        StoreResponse response = mm.map(store, StoreResponse.class);
        if (store.getGroup() != null) {
            response.setGroupId(store.getGroup().getId());
        }
        response.setStoreInfoResponse(StoreInfoResponse.fromEntity(store));
        return response;
    }
}
