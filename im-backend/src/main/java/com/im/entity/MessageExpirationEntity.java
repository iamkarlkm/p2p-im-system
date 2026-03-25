package com.im.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 消息过期策略实体
 * 支持会话级过期策略和阅后即焚模式
 */
@Entity
@Table(name = "im_message_expiration", indexes = {
    @Index(name = "idx_conversation_id", columnList = "conversationId"),
    @Index(name = "idx_expire_at", columnList = "expireAt"),
    @Index(name = "idx_sender_receiver", columnList = "senderId, receiverId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageExpirationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 会话ID (私聊为空，群聊为群ID) */
    @Column(length = 64)
    private String conversationId;

    /** 发送者用户ID (私聊时使用) */
    @Column(length = 64)
    private String senderId;

    /** 接收者用户ID (私聊时使用) */
    @Column(length = 64)
    private String receiverId;

    /**
     * 过期类型:
     * - DURATION: 固定时长后过期 (单位: 秒)
     * - READ_ONCE: 阅后即焚 (读取后倒计时)
     * - SCHEDULE: 定时过期 (指定日期时间)
     * - OFF: 关闭过期
     */
    @Column(length = 20, nullable = false)
    private String expirationType;

    /**
     * 过期时长 (秒)
     * DURATION: 消息发出后 N 秒过期
     * READ_ONCE: 读取后 N 秒过期
     */
    @Column(nullable = false)
    @Builder.Default
    private Long durationSeconds = 0L;

    /** 过期时间点 (用于 SCHEDULE 类型) */
    @Column
    private LocalDateTime expireAt;

    /**
     * 是否启用
     * true: 启用过期策略
     * false: 关闭过期策略
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    /** 创建者用户ID */
    @Column(length = 64)
    private String createdBy;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

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
