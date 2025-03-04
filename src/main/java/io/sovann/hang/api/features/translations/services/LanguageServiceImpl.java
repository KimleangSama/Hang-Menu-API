package io.sovann.hang.api.features.translations.services;

import io.sovann.hang.api.exceptions.*;
import io.sovann.hang.api.features.stores.entities.*;
import io.sovann.hang.api.features.stores.services.*;
import io.sovann.hang.api.features.translations.entities.*;
import io.sovann.hang.api.features.translations.payloads.requests.*;
import io.sovann.hang.api.features.translations.payloads.responses.*;
import io.sovann.hang.api.features.translations.repos.*;
import io.sovann.hang.api.features.users.entities.*;
import java.util.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl {
    private final LanguageRepository languageRepository;
    private final StoreServiceImpl storeService;

    @Transactional
    @CacheEvict(value = "languages", key = "#request.storeId")
    public LanguageResponse createLanguage(User user, CreateLanguageRequest request) {
        Store store = storeService.getStoreEntityById(user, request.getStoreId());
        if (store == null) {
            throw new ResourceNotFoundException("Store", request.getStoreId().toString());
        }
        Language language = CreateLanguageRequest.fromRequest(request);
        language.setStore(store);
        languageRepository.save(language);
        return LanguageResponse.fromEntity(language);
    }

    @Transactional
    @Cacheable(value = "languages", key = "#storeId")
    public List<LanguageResponse> listLanguages(UUID storeId) {
        List<Language> languages = languageRepository.findAllByStoreId(storeId);
        return LanguageResponse.fromEntities(languages);
    }

    @Transactional
    @CacheEvict(value = "code", key = "#languageCode")
    public boolean isLanguageExist(String languageCode) {
        return languageRepository.existsByCode(languageCode);
    }
}
