package io.sovann.hang.api.features.stores.payloads.request;

import io.sovann.hang.api.configs.MMConfig;
import io.sovann.hang.api.features.stores.entities.Language;
import io.sovann.hang.api.features.stores.entities.Store;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.List;

@Slf4j
@Getter
@Setter
@ToString
public class CreateLanguageRequest {
    private String language;

    public static Language fromRequest(CreateLanguageRequest request) {
        ModelMapper mapper = MMConfig.mapper();
        return mapper.map(request, Language.class);
    }

    public static List<Language> fromRequests(List<CreateLanguageRequest> languageRequests, Store store) {
        ModelMapper mapper = MMConfig.mapper();
        return languageRequests.stream()
                .map(oh -> {
                    Language entity = mapper.map(oh, Language.class);
                    entity.setStore(store);
                    return entity;
                })
                .toList();
    }
}
