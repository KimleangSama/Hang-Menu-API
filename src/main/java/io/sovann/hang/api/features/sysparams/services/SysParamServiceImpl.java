package io.sovann.hang.api.features.sysparams.services;

import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.sysparams.entities.*;
import io.sovann.hang.api.features.sysparams.repos.*;
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
