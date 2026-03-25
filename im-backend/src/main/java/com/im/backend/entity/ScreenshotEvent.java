package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenshotEvent {
    private String eventId;
    private Long userId;
    private Long conversationId;
    private String conversationType;
    private Long capturedByUserId;
    private String capturedByUsername;
    private LocalDateTime screenshotTime;
    private String deviceType;
    private String deviceInfo;
    private boolean notified;

    public ScreenshotEvent(String eventId, Long userId, Long conversationId, String conversationType) {
        this.eventId = eventId;
        this.userId = userId;
        this.conversationId = conversationId;
        this.conversationType = conversationType;
        this.screenshotTime = LocalDateTime.now();
        this.notified = false;
    }
}
