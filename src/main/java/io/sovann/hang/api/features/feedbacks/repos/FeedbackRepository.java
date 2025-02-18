package io.sovann.hang.api.features.feedbacks.repos;

import io.sovann.hang.api.features.feedbacks.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findAllByStoreId(UUID storeId);
}
