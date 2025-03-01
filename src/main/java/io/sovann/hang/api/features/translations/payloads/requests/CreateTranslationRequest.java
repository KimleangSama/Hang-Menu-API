package io.sovann.hang.api.features.translations.payloads.requests;

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
    private String languageCode;

    public static Translation fromRequest(CreateTranslationRequest request) {
        Translation translation = new Translation();
        translation.setName(request.getName());
        translation.setDescription(request.getDescription());
        translation.setLanguageCode(request.getLanguageCode());
        return translation;
    }
}
