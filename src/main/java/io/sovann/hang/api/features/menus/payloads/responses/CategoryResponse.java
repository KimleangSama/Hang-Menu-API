package io.sovann.hang.api.features.menus.payloads.responses;

import io.sovann.hang.api.features.menus.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CategoryResponse {
    private UUID id;
    private String name;
    private String description;
    private String icon;
    private boolean isHidden = false;
    private boolean isAvailable = true;

    private int position;
    private long menuCount;

    // This constructor is used to create a response object from an entity
    // DO NOT REMOVE
    public CategoryResponse(UUID id, String name, int position, long menuCount) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.menuCount = menuCount;
    }

    public static CategoryResponse fromEntity(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setIcon(category.getIcon());
        response.setHidden(category.isHidden());
        response.setAvailable(category.isAvailable());
        return response;
    }

    public static List<CategoryResponse> fromEntities(List<Category> menuCategories) {
        return menuCategories.stream().map(CategoryResponse::fromEntity).toList();
    }
}
