package com.im.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.im.backend.enums.SummaryStatus;
import com.im.backend.enums.SummaryType;
import com.im.backend.enums.SummaryQuality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能消息摘要实体
 * 基于 BART-mini/T5-small 轻量级摘要模型的自动消息摘要服务
 */
@Entity
@Table(name = "smart_message_summary",
        indexes = {
                @Index(name = "idx_summary_session_id", columnList = "sessionId"),
                @Index(name = "idx_summary_message_id", columnList = "messageId"),
                @Index(name = "idx_summary_user_id", columnList = "userId"),
                @Index(name = "idx_summary_status_type", columnList = "status, summaryType"),
                @Index(name = "idx_summary_created_at", columnList = "createdAt"),
                @Index(name = "idx_summary_quality_score", columnList = "qualityScore")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartMessageSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 会话ID - 可以是私聊或群聊会话
     */
    @NotNull(message = "会话ID不能为空")
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;

    /**
     * 消息ID - 关联的原始消息（可为空，表示会话摘要）
     */
    @Column(name = "message_id", length = 64)
    private String messageId;

    /**
     * 用户ID - 摘要的所属用户（个性化摘要）
     */
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    /**
     * 摘要状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    @Builder.Default
    private SummaryStatus status = SummaryStatus.PENDING;

    /**
     * 摘要类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "summary_type", nullable = false, length = 32)
    private SummaryType summaryType;

    /**
     * 摘要内容
     */
    @NotBlank(message = "摘要内容不能为空")
    @Column(name = "summary_content", nullable = false, columnDefinition = "TEXT")
    private String summaryContent;

    /**
     * 原消息内容（用于摘要生成，可为空）
     */
    @Column(name = "original_content", columnDefinition = "TEXT")
    private String originalContent;

    /**
     * 摘要语言代码（ISO 639-1）
     */
    @Column(name = "language_code", length = 8)
    private String languageCode;

    /**
     * 摘要质量评分
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "quality", length = 32)
    @Builder.Default
    private SummaryQuality quality = SummaryQuality.MEDIUM;

    /**
     * 质量评分数值（0-100）
     */
    @Column(name = "quality_score")
    @Builder.Default
    private Integer qualityScore = 70;

    /**
     * 摘要长度（字符数）
     */
    @Column(name = "summary_length")
    private Integer summaryLength;

    /**
     * 目标摘要长度（用户个性化设置）
     */
    @Column(name = "target_length")
    private Integer targetLength;

    /**
     * 摘要版本号（支持历史版本）
     */
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    /**
     * 摘要风格（如：简洁/详细/要点式/叙事式）
     */
    @Column(name = "summary_style", length = 64)
    private String summaryStyle;

    /**
     * 关键信息提取列表（JSON 格式）
     */
    @Column(name = "key_points", columnDefinition = "JSON")
    @Convert(converter = KeyPointsConverter.class)
    private List<String> keyPoints;

    /**
     * 元数据（JSON 格式）
     * 包含：模型版本、生成时间、置信度、处理时间等
     */
    @Column(name = "metadata", columnDefinition = "JSON")
    @Convert(converter = MetadataConverter.class)
    private Map<String, Object> metadata;

    /**
     * 用户反馈评分（1-5）
     */
    @Column(name = "user_rating")
    private Integer userRating;

    /**
     * 用户反馈内容
     */
    @Column(name = "user_feedback", length = 500)
    private String userFeedback;

    /**
     * 是否标记为喜欢
     */
    @Column(name = "is_favorite")
    @Builder.Default
    private Boolean isFavorite = false;

    /**
     * 是否启用离线缓存
     */
    @Column(name = "offline_cached")
    @Builder.Default
    private Boolean offlineCached = false;

    /**
     * 缓存失效时间
     */
    @Column(name = "cache_expiry_time")
    private LocalDateTime cacheExpiryTime;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 摘要生成时间
     */
    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    /**
     * 是否已读
     */
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    /**
     * 阅读时间
     */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * 共享用户ID列表（JSON 格式）
     */
    @Column(name = "shared_user_ids", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> sharedUserIds;

    /**
     * 标签列表（用于分类和搜索）
     */
    @Column(name = "tags", columnDefinition = "JSON")
    @Convert(converter = StringListConverter.class)
    private List<String> tags;

    /**
     * 业务数据（额外业务字段，JSON 格式）
     */
    @Column(name = "business_data", columnDefinition = "JSON")
    @Convert(converter = MetadataConverter.class)
    private Map<String, Object> businessData;

    /**
     * 逻辑删除标志
     */
    @Column(name = "deleted")
    @Builder.Default
    private Boolean deleted = false;

    /**
     * 删除时间
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        if (summaryLength == null && summaryContent != null) {
            summaryLength = summaryContent.length();
        }
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }

    /**
     * 获取关键点数量
     */
    @Transient
    public Integer getKeyPointsCount() {
        return keyPoints != null ? keyPoints.size() : 0;
    }

    /**
     * 检查摘要是否过期（缓存失效）
     */
    @Transient
    public Boolean isExpired() {
        return cacheExpiryTime != null && LocalDateTime.now().isAfter(cacheExpiryTime);
    }

    /**
     * 检查摘要质量是否为高
     */
    @Transient
    public Boolean isHighQuality() {
        return quality == SummaryQuality.HIGH || (qualityScore != null && qualityScore >= 80);
    }

    /**
     * 检查是否需要重新生成（基于质量评分）
     */
    @Transient
    public Boolean needsRegeneration() {
        return qualityScore != null && qualityScore < 60;
    }

    /**
     * 字符串列表转换器
     */
    @Converter
    public static class StringListConverter implements AttributeConverter<List<String>, String> {
        @Override
        public String convertToDatabaseColumn(List<String> attribute) {
            return attribute != null ? String.join(",", attribute) : null;
        }

        @Override
        public List<String> convertToEntityAttribute(String dbData) {
            return dbData != null ? List.of(dbData.split(",")) : null;
        }
    }

    /**
     * 关键点转换器
     */
    @Converter
    public static class KeyPointsConverter implements AttributeConverter<List<String>, String> {
        @Override
        public String convertToDatabaseColumn(List<String> attribute) {
            return attribute != null ? String.join("|", attribute) : null;
        }

        @Override
        public List<String> convertToEntityAttribute(String dbData) {
            return dbData != null ? List.of(dbData.split("\\|")) : null;
        }
    }

    /**
     * 元数据转换器
     */
    @Converter
    public static class MetadataConverter implements AttributeConverter<Map<String, Object>, String> {
        @Override
        public String convertToDatabaseColumn(Map<String, Object> attribute) {
            return attribute != null ? attribute.toString() : null;
        }

        @Override
        public Map<String, Object> convertToEntityAttribute(String dbData) {
            return dbData != null ? Map.of() : null; // 简化实现，实际应解析 JSON
        }
    }
}