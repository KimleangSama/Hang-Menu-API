package io.sovann.hang.api.features.menus.payloads.responses;

import io.sovann.hang.api.features.menus.entities.Category;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CategoryResponse {
    private UUID id;
    private String name;
    private String description;
    private String icon;
    private boolean isHidden = false;
    private boolean isAvailable = true;

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
