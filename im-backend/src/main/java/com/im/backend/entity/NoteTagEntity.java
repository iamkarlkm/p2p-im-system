package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 笔记标签实体
 * 用户自定义标签，用于组织和分类会话笔记
 */
@Entity
@Table(name = "note_tags", indexes = {
    @Index(name = "idx_tag_user", columnList = "userId"),
    @Index(name = "idx_tag_name", columnList = "tagName")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属用户 */
    @Column(nullable = false)
    private Long userId;

    /** 标签名称 */
    @Column(nullable = false, length = 64)
    private String tagName;

    /** 标签颜色 */
    @Column(length = 7)
    @Builder.Default
    private String color = "#90CAF9";

    /** 标签图标 (emoji) */
    @Column(length = 8)
    @Builder.Default
    private String icon = "🏷️";

    /** 使用次数 */
    @Column(nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 排序权重 */
    @Column(nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
