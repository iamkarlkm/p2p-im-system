package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 用户翻译设置实体
 */
@Data
@Entity
@Table(name = "im_translation_settings")
public class TranslationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(nullable = false, unique = true)
    private Long userId;

    /** 是否启用自动翻译 */
    @Column(nullable = false)
    private Boolean autoTranslate = false;

    /** 首选目标语言 */
    @Column(length = 10)
    private String preferredTargetLang = "zh-CN";

    /** 自动翻译的语言白名单（为空表示翻译所有） */
    @Column(columnDefinition = "TEXT")
    private String autoLangWhitelist;

    /** 翻译服务提供商 */
    @Column(length = 20)
    private String provider = "OPENAI";

    /** API Key（加密存储） */
    private String apiKey;

    /** 是否显示原文 */
    @Column(nullable = false)
    private Boolean showOriginal = true;

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
