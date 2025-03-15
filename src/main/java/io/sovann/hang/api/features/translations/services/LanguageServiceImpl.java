package io.sovann.hang.api.features.translations.services;

import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.stores.services.StoreServiceImpl;
import io.sovann.hang.api.features.translations.entities.Language;
import io.sovann.hang.api.features.translations.payloads.requests.CreateLanguageRequest;
import io.sovann.hang.api.features.translations.payloads.responses.LanguageResponse;
import io.sovann.hang.api.features.translations.repos.LanguageRepository;
import io.sovann.hang.api.features.users.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
