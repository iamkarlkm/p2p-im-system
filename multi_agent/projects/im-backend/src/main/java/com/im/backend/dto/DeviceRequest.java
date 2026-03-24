package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceRequest {
    private Long deviceId;
    private String deviceToken;
    private String deviceType;
    private String deviceName;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
    private String browserInfo;
    private String ipAddress;
    private String location;
    private Double latitude;
    private Double longitude;
    private Boolean isTrusted;
}
