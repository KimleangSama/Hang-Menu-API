package io.sovann.hang.api.features.translations.services;

import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.menus.entities.Menu;
import io.sovann.hang.api.features.menus.payloads.responses.MenuResponse;
import io.sovann.hang.api.features.menus.services.MenuServiceImpl;
import io.sovann.hang.api.features.translations.entities.Translation;
import io.sovann.hang.api.features.translations.payloads.requests.CreateTranslationRequest;
import io.sovann.hang.api.features.translations.repos.TranslationRepository;
import io.sovann.hang.api.features.users.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl {
    private final TranslationRepository translationRepository;
    private final MenuServiceImpl menuService;
    private final LanguageServiceImpl languageServiceImpl;

    @Transactional
    @CacheEvict(value = "translations", key = "#request.menuId")
    public MenuResponse createTranslation(User user, CreateTranslationRequest request) {
        Menu menu = menuService.findMenuEntityById(request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu", request.getMenuId().toString()));
        boolean isLanguageExist = languageServiceImpl.isLanguageExist(request.getLanguageCode());
        if (!isLanguageExist) {
            throw new ResourceNotFoundException("Language", request.getLanguageCode());
        }
        Translation translation = CreateTranslationRequest.fromRequest(request);
        translation.setMenu(menu);
        // Save translation
        translationRepository.save(translation);
        MenuResponse response = MenuResponse.fromEntity(menu);
        response.setName(translation.getName());
        response.setDescription(translation.getDescription());
        return response;
    }
}
