package io.sovann.hang.api.features.translations.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.commons.controllers.*;
import io.sovann.hang.api.features.commons.payloads.*;
import io.sovann.hang.api.features.menus.payloads.responses.*;
import io.sovann.hang.api.features.translations.payloads.requests.CreateTranslationRequest;
import io.sovann.hang.api.features.translations.services.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.utils.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.TRANSLATION)
@RequiredArgsConstructor
public class TranslationController {
    private final TranslationServiceImpl translationService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    public BaseResponse<MenuResponse> createTranslation(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateTranslationRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> translationService.createTranslation(user.getUser(), request),
                "Language failed to create",
                null);
    }
}
