package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "signal_session", indexes = {
    @Index(name = "idx_room_id", columnList = "roomId"),
    @Index(name = "idx_caller_id", columnList = "callerId"),
    @Index(name = "idx_callee_id", columnList = "calleeId"),
    @Index(name = "idx_session_status", columnList = "status")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String roomId;

    @Column(nullable = false)
    private Long callerId;

    @Column(nullable = false)
    private Long calleeId;

    @Column(nullable = false, length = 32)
    private String callType; // AUDIO, VIDEO

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SignalStatus status;

    @Column(length = 512)
    private String callerSdp;

    @Column(length = 512)
    private String calleeSdp;

    @Column(length = 256)
    private String callerIceCandidate;

    @Column(length = 256)
    private String calleeIceCandidate;

    private LocalDateTime ringingAt;

    private LocalDateTime acceptedAt;

    private LocalDateTime endedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public enum SignalStatus {
        PENDING,      // 等待接听
        RINGING,      // 响铃中
        ACCEPTED,     // 已接听
        REJECTED,     // 拒绝
        BUSY,         // 忙线
        NO_ANSWER,    // 无应答
        CANCELLED,    // 已取消
        ENDED         // 已结束
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
