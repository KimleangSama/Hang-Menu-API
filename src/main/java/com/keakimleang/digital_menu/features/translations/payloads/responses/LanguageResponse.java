package com.keakimleang.digital_menu.features.translations.payloads.responses;

import com.keakimleang.digital_menu.features.translations.entities.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class LanguageResponse {
    private UUID id;
    private UUID storeId;
    private String code;
    private String name;

    public static LanguageResponse fromEntity(Language language) {
        LanguageResponse response = new LanguageResponse();
        response.setId(language.getId());
        response.setStoreId(language.getStore().getId());
        response.setName(language.getName());
        response.setCode(language.getCode());
        return response;
    }

    public static List<LanguageResponse> fromEntities(List<Language> languages) {
        if (languages == null) {
            return Collections.emptyList();
        }
        List<LanguageResponse> responses = new ArrayList<>();
        for (Language language : languages) {
            responses.add(fromEntity(language));
        }
        return responses;
    }
}
