package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * @提及设置实体
 * 用户可以设置@提及的通知偏好
 */
@Data
@Entity
@Table(name = "im_at_mention_settings")
public class AtMentionSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(nullable = false, unique = true)
    private Long userId;

    /** 是否启用@提及提醒 */
    @Column(nullable = false)
    private Boolean enabled = true;

    /** 是否仅@所有人时提醒 */
    @Column(nullable = false)
    private Boolean onlyAtAll = false;

    /** 是否允许陌生人@时提醒 */
    @Column(nullable = false)
    private Boolean allowStrangerAt = true;

    /** 是否同步到其他设备 */
    @Column(nullable = false)
    private Boolean syncToOtherDevices = true;

    /** 免打扰开始时间（HH:mm格式） */
    private String dndStartTime;

    /** 免打扰结束时间（HH:mm格式） */
    private String dndEndTime;

    /** 是否启用免打扰 */
    @Column(nullable = false)
    private Boolean dndEnabled = false;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
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
