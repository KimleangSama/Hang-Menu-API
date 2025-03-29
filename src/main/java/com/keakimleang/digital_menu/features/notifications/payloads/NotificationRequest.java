package com.keakimleang.digital_menu.features.notifications.payloads;

import java.io.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@ToString
public class NotificationRequest implements Serializable {
    private String message;
    private LocalDateTime time;
    private String icon;
    private String receiver;
    private boolean read;
    private String type;
    private String link;
    private UUID storeId;
}
