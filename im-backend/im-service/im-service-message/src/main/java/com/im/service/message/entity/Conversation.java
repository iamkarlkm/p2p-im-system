package com.im.service.message.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 会话实体
 */
@Data
@Entity
@Table(name = "im_conversation", indexes = {
    @Index(name = "idx_conv_type", columnList = "type"),
    @Index(name = "idx_conv_updated", columnList = "updatedAt")
})
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 会话类型: SINGLE, GROUP */
    @Column(nullable = false, length = 20)
    private String type;

    /** 会话名称 */
    @Column(length = 100)
    private String name;

    /** 会话头像 */
    @Column(length = 500)
    private String avatar;

    /** 创建者ID */
    @Column(length = 36)
    private String creatorId;

    /** 最后一条消息ID */
    @Column(length = 36)
    private String lastMessageId;

    /** 最后消息时间 */
    private LocalDateTime lastMessageAt;

    /** 成员数量 */
    private Integer memberCount = 0;

    /** 是否删除 */
    private Boolean deleted = false;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
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
