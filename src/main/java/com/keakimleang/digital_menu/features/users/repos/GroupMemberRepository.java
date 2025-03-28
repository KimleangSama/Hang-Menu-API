package com.keakimleang.digital_menu.features.users.repos;

import com.keakimleang.digital_menu.features.users.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    List<GroupMember> findByGroupId(UUID id);

    Optional<GroupMember> findByGroupIdAndUserId(UUID id, UUID userId);

    Optional<GroupMember> findByUserId(UUID id);
}
