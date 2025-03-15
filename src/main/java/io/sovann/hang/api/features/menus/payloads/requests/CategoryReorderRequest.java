package io.sovann.hang.api.features.menus.payloads.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CategoryReorderRequest {
    private UUID storeId;
    private List<CategoryPositionUpdate> categories;

    @Getter
    @Setter
    public static class CategoryPositionUpdate {
        private UUID id;
        private int position;
    }
}