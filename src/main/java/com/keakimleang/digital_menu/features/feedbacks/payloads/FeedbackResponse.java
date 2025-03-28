package com.keakimleang.digital_menu.features.feedbacks.payloads;

import com.keakimleang.digital_menu.features.feedbacks.entities.*;
import com.keakimleang.digital_menu.features.feedbacks.enums.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class FeedbackResponse {
    private UUID id;
    private UUID storeId;
    private String fullname;
    private String phone;
    private String comment;
    private Rating rating;

    public static FeedbackResponse fromEntity(Feedback feedback) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId());
        response.setStoreId(feedback.getStore().getId());
        response.setFullname(feedback.getFullname());
        response.setPhone(feedback.getPhone());
        response.setComment(feedback.getComment());
        response.setRating(feedback.getRating());
        return response;
    }

    public static List<FeedbackResponse> fromEntities(List<Feedback> feedbacks) {
        if (feedbacks == null) {
            return Collections.emptyList();
        }
        List<FeedbackResponse> responses = new ArrayList<>();
        for (Feedback feedback : feedbacks) {
            responses.add(fromEntity(feedback));
        }
        return responses;
    }
}
