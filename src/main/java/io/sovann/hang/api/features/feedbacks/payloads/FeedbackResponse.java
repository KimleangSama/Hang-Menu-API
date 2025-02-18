package io.sovann.hang.api.features.feedbacks.payloads;

import io.sovann.hang.api.features.feedbacks.entities.Feedback;
import io.sovann.hang.api.features.feedbacks.enums.Rating;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
