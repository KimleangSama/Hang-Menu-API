package io.sovann.hang.api.features.sysparams.repos;

import io.sovann.hang.api.features.sysparams.entities.SysParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SysParamRepository extends JpaRepository<SysParam, UUID> {
    Optional<SysParam> findByStoreId(UUID storeId);
}
