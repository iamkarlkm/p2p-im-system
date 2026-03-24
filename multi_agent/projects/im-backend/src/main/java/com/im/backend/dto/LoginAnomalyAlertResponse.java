package com.im.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAnomalyAlertResponse {
    private Long id;
    private Long userId;
    private String alertType;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String ipAddress;
    private String location;
    private LocalDateTime loginTime;
    private Boolean isConfirmed;
    private LocalDateTime confirmedAt;
    private Boolean isDismissed;
    private LocalDateTime dismissedAt;
    private Integer riskScore;
    private String riskFactors;
    private String actionTaken;
    private LocalDateTime createdAt;
}
