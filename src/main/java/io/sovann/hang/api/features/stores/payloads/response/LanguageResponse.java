package io.sovann.hang.api.features.stores.payloads.response;

import io.sovann.hang.api.features.translations.entities.Language;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class LanguageResponse {
    private UUID id;
    private String language;

    public static LanguageResponse fromEntity(Language language) {
        LanguageResponse response = new LanguageResponse();
        response.setId(language.getId());
        response.setLanguage(language.getName());
        return response;
    }

    public static List<LanguageResponse> fromEntities(List<Language> operatingHours) {
        if (operatingHours == null) {
            return List.of();
        }
        return operatingHours.stream().map(LanguageResponse::fromEntity).collect(Collectors.toList());
    }
}
