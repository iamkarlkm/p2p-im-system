package com.im.service.group.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 群组实体
 */
@Data
@Entity
@Table(name = "im_group", indexes = {
    @Index(name = "idx_group_owner", columnList = "ownerId"),
    @Index(name = "idx_group_type", columnList = "type")
})
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** 群组名称 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 群组描述 */
    @Column(length = 500)
    private String description;

    /** 群组头像 */
    @Column(length = 500)
    private String avatar;

    /** 群组类型: PUBLIC, PRIVATE */
    @Column(nullable = false, length = 20)
    private String type;

    /** 群主ID */
    @Column(nullable = false, length = 36)
    private String ownerId;

    /** 成员数量 */
    private Integer memberCount = 0;

    /** 最大成员数 */
    private Integer maxMembers = 500;

    /** 是否已解散 */
    private Boolean dissolved = false;

    private LocalDateTime dissolvedAt;

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
