package com.im.server.push;

import java.time.LocalDateTime;

/**
 * 设备Token实体
 * 
 * 用于管理用户设备的推送Token，支持多平台、多设备
 */
public class DeviceToken {

    public enum Platform {
        IOS("iOS"),
        ANDROID("Android"),
        WINDOWS("Windows"),
        MACOS("macOS"),
        WEB("Web"),
        LINUX("Linux"),
        UNKNOWN("Unknown");

        private final String value;

        Platform(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Platform fromString(String value) {
            if (value == null) return UNKNOWN;
            for (Platform p : values()) {
                if (p.value.equalsIgnoreCase(value) || p.name().equalsIgnoreCase(value)) {
                    return p;
                }
            }
            return UNKNOWN;
        }
    }

    public enum DeviceType {
        PHONE("手机"),
        TABLET("平板"),
        DESKTOP("桌面电脑"),
        WEB_BROWSER("浏览器"),
        TV("电视"),
        WATCH("手表"),
        CAR("车载"),
        UNKNOWN("未知");

        private final String value;

        DeviceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static DeviceType fromString(String value) {
            if (value == null) return UNKNOWN;
            for (DeviceType d : values()) {
                if (d.value.equalsIgnoreCase(value) || d.name().equalsIgnoreCase(value)) {
                    return d;
                }
            }
            return UNKNOWN;
        }
    }

    private Long id;
    private Long userId;              // 用户ID
    private String deviceToken;       // 推送Token
    private Platform platform;        // 平台: IOS, ANDROID, etc.
    private DeviceType deviceType;    // 设备类型
    private String deviceName;         // 设备名称 (如 "iPhone 15 Pro")
    private String deviceModel;       // 设备型号
    private String osVersion;         // 操作系统版本
    private String appVersion;        // App版本
    private String bundleId;          // iOS Bundle ID
    private String packageName;       // Android包名
    private String channel;           // 推送通道: FCM, HMS, XM, OPPO, VIVO, JPUSH, GETUI
    private boolean enabled = true;   // 是否启用推送
    private boolean sandbox;          // iOS沙盒环境
    private String voipToken;         // iOS VoIP Token
    private boolean voipEnabled = false;
    private String badge;             // iOS角标数字
    private String sound;             // 推送声音
    private LocalDateTime lastActiveTime;   // 最后活跃时间
    private LocalDateTime tokenUpdatedTime; // Token更新时间
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String extension;         // 扩展数据 (JSON)

    // ==================== Getters and Setters ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }

    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }

    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public String getBundleId() { return bundleId; }
    public void setBundleId(String bundleId) { this.bundleId = bundleId; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isSandbox() { return sandbox; }
    public void setSandbox(boolean sandbox) { this.sandbox = sandbox; }

    public String getVoipToken() { return voipToken; }
    public void setVoipToken(String voipToken) { this.voipToken = voipToken; }

    public boolean isVoipEnabled() { return voipEnabled; }
    public void setVoipEnabled(boolean voipEnabled) { this.voipEnabled = voipEnabled; }

    public String getBadge() { return badge; }
    public void setBadge(String badge) { this.badge = badge; }

    public String getSound() { return sound; }
    public void setSound(String sound) { this.sound = sound; }

    public LocalDateTime getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(LocalDateTime lastActiveTime) { this.lastActiveTime = lastActiveTime; }

    public LocalDateTime getTokenUpdatedTime() { return tokenUpdatedTime; }
    public void setTokenUpdatedTime(LocalDateTime tokenUpdatedTime) { this.tokenUpdatedTime = tokenUpdatedTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    @Override
    public String toString() {
        return "DeviceToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", platform=" + platform +
                ", deviceType=" + deviceType +
                ", channel='" + channel + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
