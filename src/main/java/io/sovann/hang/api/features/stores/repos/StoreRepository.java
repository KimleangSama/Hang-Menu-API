package io.sovann.hang.api.features.stores.repos;

import io.sovann.hang.api.features.stores.entities.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {
    Optional<Store> findBySlug(String slug);

    Optional<Store> findByGroupId(UUID id);
}
