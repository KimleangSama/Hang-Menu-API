package io.sovann.hang.api.features.translations.payloads.requests;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.translations.entities.Language;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.UUID;

@Slf4j
@Getter
@Setter
@ToString
public class CreateLanguageRequest {
    private String code;
    private String name;
    private UUID storeId;

    public static Language fromRequest(CreateLanguageRequest request) {
        ModelMapper mapper = MMConfig.mapper();
        return mapper.map(request, Language.class);
    }
}
