package io.sovann.hang.api.features.stores.repos;

import io.sovann.hang.api.features.stores.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
    List<Store> findAllByGroupIdOrderByCreatedAt(UUID groupId);

    Optional<Store> findBySlug(String slug);

    Optional<Store> findByGroupId(UUID id);
}
