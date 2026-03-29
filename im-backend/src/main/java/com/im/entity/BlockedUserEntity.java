package com.im.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户黑名单实体
 * 记录用户拉黑的其他用户信息
 */
@Data
@Entity
@Table(name = "blocked_users",
       uniqueConstraints = @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"}),
       indexes = {
           @Index(name = "idx_blocker_id", columnList = "blocker_id"),
           @Index(name = "idx_blocked_id", columnList = "blocked_id")
       })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedUserEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 拉黑者ID (主动拉黑的用户)
     */
    @Column(name = "blocker_id", nullable = false)
    private Long blockerId;
    
    /**
     * 被拉黑者ID (被屏蔽的用户)
     */
    @Column(name = "blocked_id", nullable = false)
    private Long blockedId;
    
    /**
     * 拉黑原因 (可选)
     */
    @Column(name = "reason", length = 500)
    private String reason;
    
    /**
     * 拉黑时间
     */
    @Column(name = "blocked_at", nullable = false)
    private LocalDateTime blockedAt;
    
    /**
     * 是否隐藏在线状态
     */
    @Column(name = "hide_online_status", nullable = false)
    private Boolean hideOnlineStatus;
    
    /**
     * 是否静音消息
     */
    @Column(name = "mute_messages", nullable = false)
    private Boolean muteMessages;
    
    @PrePersist
    protected void onCreate() {
        blockedAt = LocalDateTime.now();
        if (hideOnlineStatus == null) hideOnlineStatus = true;
        if (muteMessages == null) muteMessages = true;
    }
}
