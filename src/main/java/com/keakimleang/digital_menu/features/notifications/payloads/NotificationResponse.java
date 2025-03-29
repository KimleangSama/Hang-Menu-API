package com.keakimleang.digital_menu.features.notifications.payloads;

import com.keakimleang.digital_menu.configs.*;
import com.keakimleang.digital_menu.features.notifications.entities.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Slf4j
@Getter
@Setter
@ToString
public class NotificationResponse {
    private UUID id;
    private String message;
    private LocalDateTime time;
    private String icon;
    private String receiver;
    private boolean read;
    private String type;
    private String link;
    private UUID storeId;

    public static NotificationResponse fromEntity(Notification notification) {
        var response = new NotificationResponse();
        MMConfig.mapper().map(notification, response);
        if (notification.getStore() != null) {
            response.setStoreId(notification.getStore().getId());
        }
        return response;
    }

    public static List<NotificationResponse> fromEntities(List<Notification> notifications) {
        var responses = new ArrayList<NotificationResponse>();
        for (var notification : notifications) {
            try {
                responses.add(fromEntity(notification));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return responses;
    }
}
