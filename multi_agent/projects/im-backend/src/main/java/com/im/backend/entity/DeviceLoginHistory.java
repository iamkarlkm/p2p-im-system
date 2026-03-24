package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@Entity
@Table(name = "device_login_history", indexes = {
    @Index(name = "idx_login_history_user", columnList = "userId"),
    @Index(name = "idx_login_history_device", columnList = "deviceId"),
    @Index(name = "idx_login_history_time", columnList = "loginTime")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceLoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column
    private Long deviceId;

    @Column(nullable = false)
    private String deviceToken;

    @Column(length = 64)
    private String deviceType;

    @Column(length = 128)
    private String deviceName;

    @Column(length = 256)
    private String ipAddress;

    @Column(length = 128)
    private String location;

    @Column(nullable = false)
    private Instant loginTime;

    @Column
    private Instant logoutTime;

    @Column(nullable = false)
    private String action;

    @Column(length = 64)
    private String loginStatus;

    @PrePersist
    public void prePersist() {
        if (loginTime == null) loginTime = Instant.now();
    }
}
