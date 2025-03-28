package com.keakimleang.digital_menu.features.translations.payloads.requests;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.translations.entities.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.modelmapper.*;

@Slf4j
@Getter
@Setter
@ToString
public class CreateLanguageRequest {
    private String code;
    private String name;
    private UUID storeId;

    public static Language fromRequest(CreateLanguageRequest request) {
        ModelMapper mapper = MMConfig.mapper();
        return mapper.map(request, Language.class);
    }
}
