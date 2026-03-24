package com.im.backend.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", nullable = false, unique = true)
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "device_model")
    private String deviceModel;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "location")
    private String location;

    @Column(name = "last_active_time")
    private LocalDateTime lastActiveTime;

    @Column(name = "first_login_time", nullable = false)
    private LocalDateTime firstLoginTime;

    @Column(name = "is_current")
    private Boolean isCurrent;

    @Column(name = "is_trusted")
    private Boolean isTrusted;

    @Column(name = "is_remote_terminated")
    private Boolean isRemoteTerminated;

    @Column(name = "terminated_at")
    private LocalDateTime terminatedAt;

    @Column(name = "push_token")
    private String pushToken;

    @Column(name = "fingerprint")
    private String fingerprint;

    @PrePersist
    protected void onCreate() {
        firstLoginTime = LocalDateTime.now();
        lastActiveTime = LocalDateTime.now();
        if (isCurrent == null) isCurrent = false;
        if (isTrusted == null) isTrusted = false;
        if (isRemoteTerminated == null) isRemoteTerminated = false;
    }
}
