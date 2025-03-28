package com.keakimleang.digital_menu.features.sysparams.services;

import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.sysparams.entities.*;
import com.keakimleang.digital_menu.features.sysparams.repos.*;
import java.util.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class SysParamServiceImpl {
    private final SysParamRepository sysParamRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheValue.SYS_PARAM, key = "#storeId")
    public SysParam findSysParamByStoreId(UUID storeId) {
        return sysParamRepository.findByStoreId(storeId)
                .orElse(null);
    }

}
