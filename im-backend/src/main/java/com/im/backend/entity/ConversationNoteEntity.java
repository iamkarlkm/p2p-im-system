package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 会话笔记实体
 * 支持在会话中添加笔记、标注消息、管理标签
 */
@Entity
@Table(name = "conversation_notes", indexes = {
    @Index(name = "idx_note_conversation", columnList = "conversationId"),
    @Index(name = "idx_note_user", columnList = "userId"),
    @Index(name = "idx_note_created", columnList = "createdAt"),
    @Index(name = "idx_note_pinned", columnList = "pinned")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属用户 */
    @Column(nullable = false)
    private Long userId;

    /** 所属会话ID */
    @Column(nullable = false)
    private Long conversationId;

    /** 笔记标题 */
    @Column(length = 255)
    private String title;

    /** 笔记内容 (富文本/Markdown) */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 笔记颜色 (hex) */
    @Column(length = 7)
    @Builder.Default
    private String color = "#FFF9C4";

    /** 是否置顶 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean pinned = false;

    /** 是否加密 */
    @Column(nullable = false)
    @Builder.Default
    private Boolean encrypted = false;

    /** 加密密钥版本 */
    private Integer encryptionVersion;

    /** 笔记来源: MANUAL/CALLED_BY_MESSAGE */
    @Column(length = 32)
    @Builder.Default
    private String source = "MANUAL";

    /** 来源消息ID (如果从消息标注创建) */
    private Long sourceMessageId;

    /** 表情反应统计 JSON: {"emoji": count} */
    @Column(columnDefinition = "JSON")
    private String reactions;

    /** 反应数量 */
    @Column(nullable = false)
    @Builder.Default
    private Integer reactionCount = 0;

    /** 标签列表 (逗号分隔 或 JSON 数组) */
    @Column(columnDefinition = "TEXT")
    private String tags;

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

    /** 删除时间 */
    private LocalDateTime deletedAt;

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
