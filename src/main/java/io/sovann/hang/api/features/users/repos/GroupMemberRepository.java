package io.sovann.hang.api.features.users.repos;

import io.sovann.hang.api.features.users.entities.GroupMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    Page<GroupMember> findByGroupId(UUID id, Pageable pageable);

    long countByGroupId(UUID id);

    Optional<GroupMember> findByGroupIdAndUserId(UUID id, UUID userId);
}
