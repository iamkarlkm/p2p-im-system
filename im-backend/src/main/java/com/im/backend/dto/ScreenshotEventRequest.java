package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScreenshotEventRequest {
    private Long conversationId;
    private String conversationType;
    private String deviceType;
    private String deviceInfo;
}
