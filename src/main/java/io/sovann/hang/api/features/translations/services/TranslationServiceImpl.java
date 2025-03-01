package io.sovann.hang.api.features.translations.services;

import io.sovann.hang.api.exceptions.*;
import io.sovann.hang.api.features.menus.entities.*;
import io.sovann.hang.api.features.menus.payloads.responses.*;
import io.sovann.hang.api.features.menus.services.*;
import io.sovann.hang.api.features.translations.entities.*;
import io.sovann.hang.api.features.translations.payloads.requests.CreateTranslationRequest;
import io.sovann.hang.api.features.translations.repos.*;
import io.sovann.hang.api.features.users.entities.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl {
    private final TranslationRepository translationRepository;
    private final MenuServiceImpl menuService;

    @Transactional
    @CacheEvict(value = "translations", key = "#request")
    public MenuResponse createTranslation(User user, CreateTranslationRequest request) {
        Menu menu = menuService.getMenuEntityById(request.getMenuId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu", request.getMenuId().toString()));
        Translation translation = CreateTranslationRequest.fromRequest(request);
        translation.setMenu(menu);
        translationRepository.save(translation);
        MenuResponse response = MenuResponse.fromEntity(menu);
        response.setName(translation.getName());
        response.setDescription(translation.getDescription());
        return response;
    }
}
