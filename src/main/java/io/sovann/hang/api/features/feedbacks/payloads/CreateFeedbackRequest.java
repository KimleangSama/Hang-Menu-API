package io.sovann.hang.api.features.feedbacks.payloads;

import io.sovann.hang.api.features.feedbacks.entities.Feedback;
import io.sovann.hang.api.features.feedbacks.enums.Rating;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateFeedbackRequest {
    private String fullname;
    private String phone;
    private String comment;
    private Rating rating;

    public static Feedback fromRequest(CreateFeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setFullname(request.getFullname());
        feedback.setPhone(request.getPhone());
        feedback.setComment(request.getComment());
        feedback.setRating(request.getRating());
        return feedback;
    }
}
