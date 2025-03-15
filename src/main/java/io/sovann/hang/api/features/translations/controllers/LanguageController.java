package io.sovann.hang.api.features.translations.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.translations.payloads.requests.CreateLanguageRequest;
import io.sovann.hang.api.features.translations.payloads.responses.LanguageResponse;
import io.sovann.hang.api.features.translations.services.LanguageServiceImpl;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
