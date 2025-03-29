package com.keakimleang.digital_menu.features.translations.services;

import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.services.*;
import com.keakimleang.digital_menu.features.translations.entities.*;
import com.keakimleang.digital_menu.features.translations.payloads.requests.*;
import com.keakimleang.digital_menu.features.translations.payloads.responses.*;
import com.keakimleang.digital_menu.features.translations.repos.*;
import com.keakimleang.digital_menu.features.users.entities.*;
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
        Store store = storeService.findStoreEntityById(user, request.getStoreId());
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
