package io.sovann.hang.api.features.users.repos;

import io.sovann.hang.api.features.users.entities.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {
    List<GroupMember> findByGroupId(UUID id);

    Optional<GroupMember> findByGroupIdAndUserId(UUID id, UUID userId);

    Optional<GroupMember> findByUserId(UUID id);
}
