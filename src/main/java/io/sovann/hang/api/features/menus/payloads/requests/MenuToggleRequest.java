package io.sovann.hang.api.features.menus.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class MenuToggleRequest {
    private UUID menuId;
    private UUID categoryId;
}
