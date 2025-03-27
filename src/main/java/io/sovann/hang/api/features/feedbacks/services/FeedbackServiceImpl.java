package io.sovann.hang.api.features.feedbacks.services;

import io.sovann.hang.api.features.feedbacks.entities.Feedback;
import io.sovann.hang.api.features.feedbacks.payloads.CreateFeedbackRequest;
import io.sovann.hang.api.features.feedbacks.payloads.FeedbackResponse;
import io.sovann.hang.api.features.feedbacks.payloads.RateResponse;
import io.sovann.hang.api.features.feedbacks.repos.FeedbackRepository;
import io.sovann.hang.api.features.stores.entities.Store;
import io.sovann.hang.api.features.stores.services.StoreServiceImpl;
import io.sovann.hang.api.features.users.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
