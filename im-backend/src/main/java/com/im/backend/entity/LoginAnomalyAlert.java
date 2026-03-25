package com.im.backend.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_anomaly_alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAnomalyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "alert_type", nullable = false)
    private String alertType;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "location")
    private String location;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "is_dismissed")
    private Boolean isDismissed;

    @Column(name = "dismissed_at")
    private LocalDateTime dismissedAt;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Column(name = "risk_factors", columnDefinition = "TEXT")
    private String riskFactors;

    @Column(name = "action_taken")
    private String actionTaken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isConfirmed == null) isConfirmed = false;
        if (isDismissed == null) isDismissed = false;
    }
}
