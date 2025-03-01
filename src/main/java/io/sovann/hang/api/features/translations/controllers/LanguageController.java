package io.sovann.hang.api.features.translations.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.commons.controllers.*;
import io.sovann.hang.api.features.commons.payloads.*;
import io.sovann.hang.api.features.translations.payloads.requests.CreateLanguageRequest;
import io.sovann.hang.api.features.translations.payloads.responses.LanguageResponse;
import io.sovann.hang.api.features.translations.services.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.utils.*;
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
