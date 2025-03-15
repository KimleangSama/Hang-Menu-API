package io.sovann.hang.api.features.menus.payloads.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CategoryMenuResponse {
    private UUID id;
    private String name;
    private int position;
    private List<MenuResponse> menus;
}
