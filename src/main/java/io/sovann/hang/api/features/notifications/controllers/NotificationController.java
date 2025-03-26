package io.sovann.hang.api.features.notifications.controllers;

import io.sovann.hang.api.annotations.*;
import io.sovann.hang.api.commons.controllers.*;
import io.sovann.hang.api.commons.payloads.*;
import io.sovann.hang.api.constants.*;
import io.sovann.hang.api.features.notifications.payloads.*;
import io.sovann.hang.api.features.notifications.services.*;
import io.sovann.hang.api.features.users.securities.*;
import io.sovann.hang.api.utils.*;
import java.util.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(APIURLs.NOTIFICATIONS)
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationServiceImpl notificationService;
    private final ControllerServiceCallback callback;

    @GetMapping("/of-store/{storeId}")
    public BaseResponse<List<NotificationResponse>> getAllByStoreId(
            @CurrentUser CustomUserDetails user,
            @PathVariable("storeId") UUID storeId
    ) {
        SoftEntityDeletable.throwErrorIfSoftDeleted(user);
        return callback.execute(() -> notificationService.getAllByStoreId(user.getUser(), storeId),
                "Failed to get notifications of store", null);
    }
}
