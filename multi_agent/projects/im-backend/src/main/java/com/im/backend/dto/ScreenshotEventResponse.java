package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenshotEventResponse {
    private String eventId;
    private Long conversationId;
    private String conversationType;
    private Long capturedByUserId;
    private String capturedByUsername;
    private LocalDateTime screenshotTime;
    private String deviceType;
    private String message;

    public static ScreenshotEventResponse fromEntity(com.im.backend.entity.ScreenshotEvent event) {
        ScreenshotEventResponse response = new ScreenshotEventResponse();
        response.setEventId(event.getEventId());
        response.setConversationId(event.getConversationId());
        response.setConversationType(event.getConversationType());
        response.setCapturedByUserId(event.getCapturedByUserId());
        response.setCapturedByUsername(event.getCapturedByUsername());
        response.setScreenshotTime(event.getScreenshotTime());
        response.setDeviceType(event.getDeviceType());
        response.setMessage(event.getCapturedByUsername() + " took a screenshot");
        return response;
    }
}
