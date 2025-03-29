package com.keakimleang.digital_menu.features.feedbacks.services;

import com.keakimleang.digital_menu.features.feedbacks.entities.*;
import com.keakimleang.digital_menu.features.feedbacks.payloads.*;
import com.keakimleang.digital_menu.features.feedbacks.repos.*;
import com.keakimleang.digital_menu.features.stores.entities.*;
import com.keakimleang.digital_menu.features.stores.services.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import java.util.*;
import lombok.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl {
    private final FeedbackRepository feedbackRepository;
    private final StoreServiceImpl storeServiceImpl;

    @Transactional
    public FeedbackResponse createFeedback(User user, CreateFeedbackRequest request) {
        Store store = storeServiceImpl.findStoreEntityById(user, request.getStoreId());
        Feedback feedback = CreateFeedbackRequest.fromRequest(request);
        feedback.setStore(store);
        if (user != null) {
            feedback.setCreatedBy(user.getId());
        }
        feedbackRepository.save(feedback);
        return FeedbackResponse.fromEntity(feedback);
    }

    @Transactional
    @Cacheable(value = "feedbacks", key = "#storeId")
    public List<FeedbackResponse> listFeedbacksOfStore(User user, UUID storeId) {
        List<Feedback> feedbacks = feedbackRepository.findAllByStoreId(storeId);
        return FeedbackResponse.fromEntities(feedbacks);
    }

    @Transactional
    @Cacheable(value = "ratings", key = "#storeId")
    public RateResponse getAverageRateOfFeedbacks(User user, UUID storeId) {
        List<Feedback> feedbacks;
        if (storeId != null) {
            feedbacks = feedbackRepository.findAllByStoreId(storeId);
        } else {
            feedbacks = feedbackRepository.findAll();
        }
        return calculateRateResponse(feedbacks);
    }

    private RateResponse calculateRateResponse(List<Feedback> feedbacks) {
        double amount = feedbacks.size();
        double rate = (amount > 0) ? feedbacks.stream().mapToDouble(f -> f.getRating().getValue()).sum() / amount : 0;
        RateResponse response = new RateResponse();
        response.setRate(rate);
        response.setAmount(amount);
        return response;
    }

}
