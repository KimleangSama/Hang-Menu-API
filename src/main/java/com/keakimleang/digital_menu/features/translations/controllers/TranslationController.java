package com.keakimleang.digital_menu.features.translations.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.menus.payloads.responses.*;
import com.keakimleang.digital_menu.features.translations.payloads.requests.*;
import com.keakimleang.digital_menu.features.translations.services.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.utils.*;
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
