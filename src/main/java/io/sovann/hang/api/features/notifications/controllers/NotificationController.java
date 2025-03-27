package io.sovann.hang.api.features.notifications.controllers;

import io.sovann.hang.api.annotations.CurrentUser;
import io.sovann.hang.api.commons.controllers.ControllerServiceCallback;
import io.sovann.hang.api.commons.payloads.BaseResponse;
import io.sovann.hang.api.constants.APIURLs;
import io.sovann.hang.api.features.notifications.payloads.MarkAsReadNotificationRequest;
import io.sovann.hang.api.features.notifications.payloads.NotificationResponse;
import io.sovann.hang.api.features.notifications.services.NotificationServiceImpl;
import io.sovann.hang.api.features.users.securities.CustomUserDetails;
import io.sovann.hang.api.utils.SoftEntityDeletable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(APIURLs.NOTIFICATIONS)
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationServiceImpl notificationService;
    private final ControllerServiceCallback callback;

    @GetMapping("/of-store/{storeId}/list")
    public BaseResponse<List<NotificationResponse>> getAllByStoreId(
            @CurrentUser CustomUserDetails user,
            @PathVariable("storeId") UUID storeId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> notificationService.getAllByStoreId(user.getUser(), storeId),
                "Failed to get notifications of store", null);
    }

    @PatchMapping("/mark-as-read")
    public BaseResponse<NotificationResponse> markAsRead(
            @CurrentUser CustomUserDetails user,
            @RequestBody MarkAsReadNotificationRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> notificationService.markAsRead(user.getUser(), request),
                "Failed to mark notification as read", null);
    }

    @DeleteMapping("/of-store/{storeId}/delete/all")
    public BaseResponse<Void> deleteAllByStoreId(
            @CurrentUser CustomUserDetails user,
            @PathVariable("storeId") UUID storeId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> notificationService.deleteAllByStoreId(user.getUser(), storeId),
                "Failed to delete all notifications of store", null);
    }
}
