package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 消息翻译记录实体
 */
@Data
@Entity
@Table(name = "im_message_translation",
       indexes = {
           @Index(name = "idx_original_message_id", columnList = "originalMessageId"),
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_translated_at", columnList = "translatedAt")
       })
public class MessageTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 原始消息ID */
    @Column(nullable = false)
    private Long originalMessageId;

    /** 用户ID（谁请求的翻译） */
    @Column(nullable = false)
    private Long userId;

    /** 原始语言代码 */
    @Column(nullable = false, length = 10)
    private String sourceLang;

    /** 目标语言代码 */
    @Column(nullable = false, length = 10)
    private String targetLang;

    /** 原始内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalContent;

    /** 翻译后内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String translatedContent;

    /** 翻译服务提供商 (OPENAI/CLAUDE/GEMINI/BAIDU/TENCENT) */
    @Column(length = 20)
    private String provider;

    /** 模型名称 */
    private String model;

    /** 翻译时间 */
    @Column(nullable = false)
    private LocalDateTime translatedAt;

    /** 是否自动翻译（vs 手动触发） */
    @Column(nullable = false)
    private Boolean autoTranslated = false;

    /** 翻译耗时（毫秒） */
    private Integer durationMs;

    @PrePersist
    protected void onCreate() {
        if (translatedAt == null) {
            translatedAt = LocalDateTime.now();
        }
    }
}
