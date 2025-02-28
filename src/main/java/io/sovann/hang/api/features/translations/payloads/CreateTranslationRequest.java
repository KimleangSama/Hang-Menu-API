package io.sovann.hang.api.features.translations.payloads;

import io.sovann.hang.api.features.translations.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateTranslationRequest {
    private UUID menuId;
    private String name;
    private String description;

    public static Translation fromRequest(CreateTranslationRequest request) {
        Translation translation = new Translation();
        translation.setName(request.getName());
        translation.setDescription(request.getDescription());
        return translation;
    }
}
