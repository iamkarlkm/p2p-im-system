package com.im.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDeviceResponse {
    private Long id;
    private Long userId;
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
    private String ipAddress;
    private String location;
    private LocalDateTime lastActiveTime;
    private LocalDateTime firstLoginTime;
    private Boolean isCurrent;
    private Boolean isTrusted;
    private Boolean isRemoteTerminated;
    private LocalDateTime terminatedAt;
}
