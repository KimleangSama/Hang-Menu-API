package com.keakimleang.digital_menu.features.translations.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.translations.payloads.requests.*;
import com.keakimleang.digital_menu.features.translations.payloads.responses.*;
import com.keakimleang.digital_menu.features.translations.services.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.LANGUAGE)
@RequiredArgsConstructor
public class LanguageController {
    private final LanguageServiceImpl languageService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    public BaseResponse<LanguageResponse> createLanguage(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateLanguageRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> languageService.createLanguage(user.getUser(), request),
                "Language failed to create",
                null);
    }

    @GetMapping("/list")
    public BaseResponse<List<LanguageResponse>> listLanguages(
            @RequestParam UUID storeId
    ) {
        return callback.execute(() -> languageService.listLanguages(storeId),
                "Languages failed to list",
                null);
    }
}
