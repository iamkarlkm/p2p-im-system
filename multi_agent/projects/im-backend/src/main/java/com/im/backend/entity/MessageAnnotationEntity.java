package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 消息标注实体
 * 将特定消息标注为重要/收藏，并可附加笔记内容
 */
@Entity
@Table(name = "message_annotations", indexes = {
    @Index(name = "idx_annot_user", columnList = "userId"),
    @Index(name = "idx_annot_message", columnList = "messageId"),
    @Index(name = "idx_annot_conversation", columnList = "conversationId"),
    @Index(name = "idx_annot_starred", columnList = "starred")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageAnnotationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属用户 */
    @Column(nullable = false)
    private Long userId;

    /** 被标注的消息ID */
    @Column(nullable = false)
    private Long messageId;

    /** 所属会话ID */
    @Column(nullable = false)
    private Long conversationId;

    /** 是否星标 (重要标记) */
    @Column(nullable = false)
    @Builder.Default
    private Boolean starred = false;

    /** 标注类型: IMPORTANT/BOOKMARK/REVIEW/PENDING/FLAGGED */
    @Column(length = 32)
    @Builder.Default
    private String annotationType = "BOOKMARK";

    /** 附加笔记内容 */
    @Column(columnDefinition = "TEXT")
    private String note;

    /** 关联的笔记ID */
    private Long linkedNoteId;

    /** 标注颜色 (hex) */
    @Column(length = 7)
    @Builder.Default
    private String color = "#FFECB3";

    /** 表情标签 */
    @Column(length = 16)
    @Builder.Default
    private String emoji = "⭐";

    /** 是否已读 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean read = true;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** 软删除 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

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
