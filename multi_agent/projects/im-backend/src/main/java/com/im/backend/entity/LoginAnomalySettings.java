package com.im.backend.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_anomaly_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAnomalySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "alert_enabled")
    private Boolean alertEnabled;

    @Column(name = "异地登录告警")
    private Boolean crossRegionAlert;

    @Column(name = "新设备告警")
    private Boolean newDeviceAlert;

    @Column(name = "异常频率告警")
    private Boolean abnormalFrequencyAlert;

    @Column(name = "未知设备告警")
    private Boolean unknownDeviceAlert;

    @Column(name = "alert_channels")
    private String alertChannels;

    @Column(name = "known_ips", columnDefinition = "TEXT")
    private String knownIps;

    @Column(name = "known_locations", columnDefinition = "TEXT")
    private String knownLocations;

    @Column(name = "max_login_attempts_per_hour")
    private Integer maxLoginAttemptsPerHour;

    @Column(name = "auto_lock_threshold")
    private Integer autoLockThreshold;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (alertEnabled == null) alertEnabled = true;
        if (crossRegionAlert == null) crossRegionAlert = true;
        if (newDeviceAlert == null) newDeviceAlert = true;
        if (abnormalFrequencyAlert == null) abnormalFrequencyAlert = true;
        if (unknownDeviceAlert == null) unknownDeviceAlert = true;
        if (maxLoginAttemptsPerHour == null) maxLoginAttemptsPerHour = 5;
        if (autoLockThreshold == null) autoLockThreshold = 10;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
