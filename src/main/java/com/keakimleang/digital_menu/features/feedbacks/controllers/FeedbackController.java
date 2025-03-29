package com.keakimleang.digital_menu.features.feedbacks.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.feedbacks.payloads.*;
import com.keakimleang.digital_menu.features.feedbacks.services.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import java.util.*;
import lombok.*;
import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

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
