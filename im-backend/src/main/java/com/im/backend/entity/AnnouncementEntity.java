package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 群公告实体
 * 支持富文本内容、置顶、有效期、已读统计
 */
@Entity
@Table(name = "im_announcement", indexes = {
    @Index(name = "idx_group_id", columnList = "groupId"),
    @Index(name = "idx_pinned", columnList = "pinned"),
    @Index(name = "idx_expire_time", columnList = "expireTime")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属群组ID */
    @Column(nullable = false)
    private Long groupId;

    /** 发布者用户ID */
    @Column(nullable = false)
    private Long authorId;

    /** 公告标题 */
    @Column(length = 200)
    private String title;

    /** 公告内容（支持 Markdown） */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** 是否置顶 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean pinned = false;

    /** 是否所有人必须阅读 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean requiredRead = false;

    /** 是否紧急公告（高亮显示） */
    @Column(nullable = false)
    @Builder.Default
    private Boolean urgent = false;

    /** 附件文件ID列表（JSON数组） */
    @Column(columnDefinition = "TEXT")
    private String attachments;

    /** 公告类型: normal/rule/notice/event */
    @Column(length = 20, nullable = false)
    @Builder.Default
    private String type = "normal";

    /** 发布时间 */
    @Column(nullable = false)
    private LocalDateTime publishTime;

    /** 过期时间（null=永不过期） */
    private LocalDateTime expireTime;

    /** 是否已删除 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    /** 删除时间 */
    private LocalDateTime deletedTime;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        publishTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** 检查公告是否在有效期内 */
    public boolean isActive() {
        if (Boolean.TRUE.equals(deleted)) return false;
        if (expireTime != null && LocalDateTime.now().isAfter(expireTime)) return false;
        return true;
    }
}
