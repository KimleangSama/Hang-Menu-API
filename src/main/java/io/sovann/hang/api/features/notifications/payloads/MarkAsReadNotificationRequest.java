package io.sovann.hang.api.features.notifications.payloads;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class MarkAsReadNotificationRequest implements Serializable {
    private UUID storeId;
    private UUID notificationId;
}
