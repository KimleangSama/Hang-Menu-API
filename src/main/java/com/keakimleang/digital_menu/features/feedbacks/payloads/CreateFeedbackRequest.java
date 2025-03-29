package com.keakimleang.digital_menu.features.feedbacks.payloads;

import com.keakimleang.digital_menu.features.feedbacks.entities.*;
import com.keakimleang.digital_menu.features.feedbacks.enums.*;
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
