package io.sovann.hang.api.features.feedbacks.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.features.commons.payloads.BaseResponse;
import io.sovann.hang.api.features.feedbacks.payloads.CreateFeedbackRequest;
import io.sovann.hang.api.features.feedbacks.payloads.FeedbackResponse;
import io.sovann.hang.api.features.feedbacks.payloads.RateResponse;
import io.sovann.hang.api.features.feedbacks.services.FeedbackServiceImpl;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(APIURLs.FEEDBACK)
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackServiceImpl feedbackService;
    private final ControllerServiceCallback callback;

    @PostMapping("/create")
    public BaseResponse<FeedbackResponse> createFeedback(
            @CurrentUser CustomUserDetails user,
            @RequestBody CreateFeedbackRequest request
    ) {
        return callback.execute(() -> feedbackService.createFeedback(user != null ? user.getUser() : null, request),
                "Feedback failed to create",
                null);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('admin', 'manager')")
    public BaseResponse<List<FeedbackResponse>> listFeedbacksOfStore(
            @CurrentUser CustomUserDetails user,
            @RequestParam UUID storeId
    ) {
        return callback.execute(() -> feedbackService.listFeedbacksOfStore(user != null ? user.getUser() : null, storeId),
                "Feedbacks failed to list",
                null);
    }

    @GetMapping("/rate")
    public BaseResponse<RateResponse> getAverageRateOfFeedbacks(
            @CurrentUser CustomUserDetails user,
            @RequestParam(required = false) UUID tableId
    ) {
        return callback.execute(() -> feedbackService.getAverageRateOfFeedbacks(user != null ? user.getUser() : null, tableId),
                "Feedbacks failed to list",
                null);
    }
}
