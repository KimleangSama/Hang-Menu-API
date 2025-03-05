package io.sovann.hang.api.features.feedbacks.payloads;

import io.sovann.hang.api.features.feedbacks.entities.*;
import io.sovann.hang.api.features.feedbacks.enums.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class CreateFeedbackRequest {
    private Rating rating;
    private String comment;
    private UUID storeId;

    public static Feedback fromRequest(CreateFeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        return feedback;
    }
}
