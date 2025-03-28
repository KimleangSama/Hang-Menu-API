package com.keakimleang.digital_menu.features.translations.services;

import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.menus.entities.*;
import com.keakimleang.digital_menu.features.menus.payloads.responses.*;
import com.keakimleang.digital_menu.features.menus.services.*;
import com.keakimleang.digital_menu.features.translations.entities.*;
import com.keakimleang.digital_menu.features.translations.payloads.requests.*;
import com.keakimleang.digital_menu.features.translations.repos.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

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
