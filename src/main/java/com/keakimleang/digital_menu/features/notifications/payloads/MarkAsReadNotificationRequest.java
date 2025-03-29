package com.keakimleang.digital_menu.features.notifications.payloads;

import java.io.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class MarkAsReadNotificationRequest implements Serializable {
    private UUID storeId;
    private UUID notificationId;
}
