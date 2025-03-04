package io.sovann.hang.api.features.menus.payloads.responses;

import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CategoryMenuResponse {
    private UUID id;
    private String name;
    private int position;
    private List<MenuResponse> menus;
}
