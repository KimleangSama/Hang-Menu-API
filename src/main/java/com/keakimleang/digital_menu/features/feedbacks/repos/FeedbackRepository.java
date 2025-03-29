package com.keakimleang.digital_menu.features.feedbacks.repos;

import com.keakimleang.digital_menu.features.feedbacks.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findAllByStoreId(UUID storeId);
}
