package com.keakimleang.digital_menu.features.sysparams.repos;

import com.keakimleang.digital_menu.features.sysparams.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface SysParamRepository extends JpaRepository<SysParam, UUID> {
    Optional<SysParam> findByStoreId(UUID storeId);
}
