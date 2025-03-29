package com.keakimleang.digital_menu.features.notifications.controllers;

import com.keakimleang.digital_menu.annotations.*;
import com.keakimleang.digital_menu.commons.controllers.*;
import com.keakimleang.digital_menu.commons.payloads.*;
import com.keakimleang.digital_menu.constants.*;
import com.keakimleang.digital_menu.features.notifications.payloads.*;
import com.keakimleang.digital_menu.features.notifications.services.*;
import com.keakimleang.digital_menu.features.users.securities.*;
import com.keakimleang.digital_menu.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.NOTIFICATIONS)
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationServiceImpl notificationService;
    private final ControllerServiceCallback callback;

    @GetMapping("/of-store/{storeId}/list")
    public BaseResponse<List<NotificationResponse>> findAllNotificationsByStoreId(
            @CurrentUser CustomUserDetails user,
            @PathVariable("storeId") UUID storeId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> notificationService.findAllNotificationsByStoreId(user.getUser(), storeId),
                "Failed to get notifications of store", null);
    }

    @PatchMapping("/mark-as-read")
    public BaseResponse<NotificationResponse> markAsReadById(
            @CurrentUser CustomUserDetails user,
            @RequestBody MarkAsReadNotificationRequest request
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> notificationService.markAsReadById(user.getUser(), request),
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
