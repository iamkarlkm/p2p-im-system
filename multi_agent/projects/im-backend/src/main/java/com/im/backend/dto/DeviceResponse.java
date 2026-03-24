package com.im.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponse {
    private Long id;
    private String userId;
    private String deviceToken;
    private String deviceType;
    private String deviceName;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
    private String browserInfo;
    private String ipAddress;
    private String location;
    private Instant createdAt;
    private Instant lastActiveAt;
    private Boolean isCurrent;
    private Instant lastLoginAt;
    private Boolean isActive;
    private Boolean isTrusted;

    private Integer activeSessionCount;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DevicePage {
        private List<DeviceResponse> items;
        private Integer page;
        private Integer size;
        private Long total;
        private Integer totalPages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginHistoryResponse {
        private Long id;
        private String userId;
        private Long deviceId;
        private String deviceToken;
        private String deviceType;
        private String deviceName;
        private String ipAddress;
        private String location;
        private Instant loginTime;
        private Instant logoutTime;
        private String action;
        private String loginStatus;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginHistoryPage {
        private List<LoginHistoryResponse> items;
        private Integer page;
        private Integer size;
        private Long total;
        private Integer totalPages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeviceStatsResponse {
        private Integer totalDevices;
        private Integer activeDevices;
        private Integer trustedDevices;
        private String mostUsedDeviceType;
        private Integer activeSessions;
    }
}
