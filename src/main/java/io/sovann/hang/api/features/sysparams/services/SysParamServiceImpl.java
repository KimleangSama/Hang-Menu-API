package io.sovann.hang.api.features.sysparams.services;

import io.sovann.hang.api.constants.CacheValue;
import io.sovann.hang.api.features.sysparams.entities.SysParam;
import io.sovann.hang.api.features.sysparams.repos.SysParamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SysParamServiceImpl {
    private final SysParamRepository sysParamRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheValue.SYS_PARAM, key = "#storeId")
    public SysParam getSysParamByStoreId(UUID storeId) {
        return sysParamRepository.findByStoreId(storeId)
                .orElse(null);
    }

}
