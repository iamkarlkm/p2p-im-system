package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@Entity
@Table(name = "device", indexes = {
    @Index(name = "idx_device_user", columnList = "userId"),
    @Index(name = "idx_device_token", columnList = "deviceToken"),
    @Index(name = "idx_device_last_active", columnList = "lastActiveAt")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true, length = 128)
    private String deviceToken;

    @Column(length = 64)
    private String deviceType;

    @Column(length = 128)
    private String deviceName;

    @Column(length = 512)
    private String deviceModel;

    @Column(length = 256)
    private String osVersion;

    @Column(length = 256)
    private String appVersion;

    @Column(length = 512)
    private String browserInfo;

    @Column(length = 256)
    private String ipAddress;

    @Column(length = 128)
    private String location;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant lastActiveAt;

    @Column(nullable = false)
    private Boolean isCurrent;

    private Instant lastLoginAt;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isTrusted;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (lastActiveAt == null) lastActiveAt = Instant.now();
        if (isCurrent == null) isCurrent = false;
        if (isActive == null) isActive = true;
        if (isTrusted == null) isTrusted = false;
    }
}
