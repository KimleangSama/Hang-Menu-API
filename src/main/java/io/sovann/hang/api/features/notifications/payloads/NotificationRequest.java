package io.sovann.hang.api.features.notifications.payloads;

import java.io.*;
import java.time.*;
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
}
