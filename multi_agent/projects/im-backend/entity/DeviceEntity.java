package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 设备实体 - 多设备管理与设备列表
 * 记录用户所有登录设备，支持同时在线设备查看、远程登出、最后活跃时间
 */
@Entity
@Table(name = "im_device",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_user_device_token", columnNames = {"userId", "deviceToken"})
       },
       indexes = {
           @Index(name = "idx_device_user", columnList = "userId"),
           @Index(name = "idx_device_token", columnList = "deviceToken"),
           @Index(name = "idx_device_status", columnList = "status"),
           @Index(name = "idx_device_last_active", columnList = "lastActiveAt"),
           @Index(name = "idx_device_type", columnList = "deviceType")
       })
public class DeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备记录ID */
    @Column(nullable = false, unique = true, length = 36)
    private String deviceId;

    /** 用户ID */
    @Column(nullable = false, length = 36)
    private String userId;

    /** 设备令牌 (用于身份验证) */
    @Column(nullable = false, unique = true, length = 128)
    private String deviceToken;

    /** 设备名称 (用户自定义) */
    @Column(length = 100)
    private String deviceName;

    /** 设备类型: IOS, ANDROID, WINDOWS, MACOS, LINUX, WEB, OTHER */
    @Column(nullable = false, length = 20)
    private String deviceType;

    /** 设备型号 */
    @Column(length = 100)
    private String deviceModel;

    /** 操作系统 */
    @Column(length = 50)
    private String osVersion;

    /** 应用版本 */
    @Column(length = 20)
    private String appVersion;

    /** 设备唯一标识符 (如 UUID) */
    @Column(length = 64)
    private String deviceIdentifier;

    /** 推送令牌 (APNs/FCM) */
    @Column(length = 256)
    private String pushToken;

    /** 推送类型: APNS, FCM, HMS, OTHER */
    @Column(length = 20)
    private String pushType;

    /** IP地址 */
    @Column(length = 45)
    private String ipAddress;

    /** 浏览器/客户端名称 */
    @Column(length = 100)
    private String clientName;

    /** 浏览器/客户端版本 */
    @Column(length = 50)
    private String clientVersion;

    /** 当前状态: ONLINE=在线, OFFLINE=离线, SUSPENDED=已暂停 */
    @Column(nullable = false, length = 20)
    private String status;

    /** 是否当前活跃设备 */
    @Column(nullable = false)
    private Boolean isCurrent;

    /** 是否可接收推送 */
    @Column(nullable = false)
    private Boolean pushEnabled;

    /** 是否隐藏此设备 (其他设备看不到) */
    @Column(nullable = false)
    private Boolean isHidden;

    /** 最后活跃时间 */
    @Column
    private LocalDateTime lastActiveAt;

    /** 最后上线时间 */
    @Column
    private LocalDateTime lastOnlineAt;

    /** 首次登录时间 */
    @Column(nullable = false)
    private LocalDateTime firstLoginAt;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 过期时间 (用于临时令牌) */
    @Column
    private LocalDateTime expiresAt;

    /** 会话ID (WebSocket等) */
    @Column(length = 64)
    private String sessionId;

    /** 设备能力: JSON数组 ["PUSH", "VOICE", "VIDEO", "FILE"] */
    @Column(length = 500)
    private String capabilities;

    /** 加密公钥 */
    @Column(columnDefinition = "TEXT")
    private String publicKey;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getDeviceModel() { return deviceModel; }
    public void setDeviceModel(String deviceModel) { this.deviceModel = deviceModel; }

    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public String getDeviceIdentifier() { return deviceIdentifier; }
    public void setDeviceIdentifier(String deviceIdentifier) { this.deviceIdentifier = deviceIdentifier; }

    public String getPushToken() { return pushToken; }
    public void setPushToken(String pushToken) { this.pushToken = pushToken; }

    public String getPushType() { return pushType; }
    public void setPushType(String pushType) { this.pushType = pushType; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientVersion() { return clientVersion; }
    public void setClientVersion(String clientVersion) { this.clientVersion = clientVersion; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsCurrent() { return isCurrent; }
    public void setIsCurrent(Boolean isCurrent) { this.isCurrent = isCurrent; }

    public Boolean getPushEnabled() { return pushEnabled; }
    public void setPushEnabled(Boolean pushEnabled) { this.pushEnabled = pushEnabled; }

    public Boolean getIsHidden() { return isHidden; }
    public void setIsHidden(Boolean isHidden) { this.isHidden = isHidden; }

    public LocalDateTime getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(LocalDateTime lastActiveAt) { this.lastActiveAt = lastActiveAt; }

    public LocalDateTime getLastOnlineAt() { return lastOnlineAt; }
    public void setLastOnlineAt(LocalDateTime lastOnlineAt) { this.lastOnlineAt = lastOnlineAt; }

    public LocalDateTime getFirstLoginAt() { return firstLoginAt; }
    public void setFirstLoginAt(LocalDateTime firstLoginAt) { this.firstLoginAt = firstLoginAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getCapabilities() { return capabilities; }
    public void setCapabilities(String capabilities) { this.capabilities = capabilities; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}
