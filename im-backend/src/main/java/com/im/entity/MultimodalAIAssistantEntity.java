package com.im.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 多模态AI助手实体类
 * 支持文本、语音、图像、视频等多种交互方式
 */
@Data
@Entity
@Table(name = "multimodal_ai_assistant", indexes = {
    @Index(name = "idx_assistant_user_id", columnList = "userId"),
    @Index(name = "idx_assistant_status", columnList = "status"),
    @Index(name = "idx_assistant_created_at", columnList = "createdAt")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MultimodalAIAssistantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 助手唯一标识
     */
    @Column(name = "assistant_id", nullable = false, unique = true, length = 64)
    @NotBlank(message = "助手ID不能为空")
    @Size(max = 64, message = "助手ID长度不能超过64")
    private String assistantId;

    /**
     * 所属用户ID
     */
    @Column(name = "user_id", nullable = false, length = 64)
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 助手名称
     */
    @Column(name = "name", nullable = false, length = 128)
    @NotBlank(message = "助手名称不能为空")
    @Size(max = 128, message = "助手名称长度不能超过128")
    private String name;

    /**
     * 助手描述
     */
    @Column(name = "description", length = 1024)
    @Size(max = 1024, message = "描述长度不能超过1024")
    private String description;

    /**
     * 助手状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AssistantStatus status = AssistantStatus.ACTIVE;

    /**
     * 支持的模态类型（多选）
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "assistant_supported_modalities", joinColumns = @JoinColumn(name = "assistant_id"))
    @Column(name = "modality")
    @Enumerated(EnumType.STRING)
    private List<ModalityType> supportedModalities;

    /**
     * 默认语言
     */
    @Column(name = "default_language", length = 16)
    private String defaultLanguage = "zh-CN";

    /**
     * 知识库ID列表（关联知识库）
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "assistant_knowledge_bases", joinColumns = @JoinColumn(name = "assistant_id"))
    @Column(name = "knowledge_base_id", length = 64)
    private List<String> knowledgeBaseIds;

    /**
     * AI模型配置（JSON格式）
     */
    @Column(name = "ai_model_config", length = 2048)
    private String aiModelConfig;

    /**
     * 个性化配置（JSON格式）
     */
    @Column(name = "personality_config", length = 2048)
    private String personalityConfig;

    /**
     * 上下文窗口大小
     */
    @Column(name = "context_window_size")
    @Min(value = 1, message = "上下文窗口大小至少为1")
    @Max(value = 100, message = "上下文窗口大小最大为100")
    private Integer contextWindowSize = 10;

    /**
     * 响应风格
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "response_style", length = 32)
    private ResponseStyle responseStyle = ResponseStyle.BALANCED;

    /**
     * 响应温度（创造性程度）
     */
    @Column(name = "temperature")
    @DecimalMin(value = "0.0", message = "温度不能小于0")
    @DecimalMax(value = "2.0", message = "温度不能大于2")
    private Double temperature = 0.7;

    /**
     * 最大响应长度
     */
    @Column(name = "max_response_length")
    @Min(value = 50, message = "最小响应长度为50")
    @Max(value = 4096, message = "最大响应长度为4096")
    private Integer maxResponseLength = 1024;

    /**
     * 是否启用语音识别
     */
    @Column(name = "voice_recognition_enabled")
    private Boolean voiceRecognitionEnabled = true;

    /**
     * 是否启用图像识别
     */
    @Column(name = "image_recognition_enabled")
    private Boolean imageRecognitionEnabled = true;

    /**
     * 是否启用视频分析
     */
    @Column(name = "video_analysis_enabled")
    private Boolean videoAnalysisEnabled = false;

    /**
     * 语音配置（JSON格式）
     */
    @Column(name = "voice_config", length = 1024)
    private String voiceConfig;

    /**
     * 视觉配置（JSON格式）
     */
    @Column(name = "vision_config", length = 1024)
    private String visionConfig;

    /**
     * 会话数量统计
     */
    @Column(name = "total_sessions")
    private Long totalSessions = 0L;

    /**
     * 消息数量统计
     */
    @Column(name = "total_messages")
    private Long totalMessages = 0L;

    /**
     * 平均响应时间（毫秒）
     */
    @Column(name = "avg_response_time_ms")
    private Double avgResponseTimeMs = 0.0;

    /**
     * 用户满意度评分（1-5分）
     */
    @Column(name = "satisfaction_score")
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    private Double satisfactionScore = 4.0;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    /**
     * 欢迎消息
     */
    @Column(name = "welcome_message", length = 512)
    private String welcomeMessage;

    /**
     * 快捷指令列表（JSON格式）
     */
    @Column(name = "quick_commands", length = 2048)
    private String quickCommands;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 最后活跃时间
     */
    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;

    /**
     * 助手状态枚举
     */
    public enum AssistantStatus {
        ACTIVE,         // 活跃
        INACTIVE,       // 非活跃
        MAINTENANCE,    // 维护中
        DISABLED,       // 已禁用
        TRAINING        // 训练中
    }

    /**
     * 模态类型枚举
     */
    public enum ModalityType {
        TEXT,           // 文本
        VOICE,          // 语音
        IMAGE,          // 图像
        VIDEO,          // 视频
        DOCUMENT,       // 文档
        CODE            // 代码
    }

    /**
     * 响应风格枚举
     */
    public enum ResponseStyle {
        CONCISE,        // 简洁
        BALANCED,       // 平衡
        DETAILED,       // 详细
        CREATIVE,       // 创意
        PROFESSIONAL,   // 专业
        FRIENDLY        // 友好
    }

    /**
     * 获取助手基本信息摘要
     */
    public String getSummary() {
        return String.format("AI助手[%s]: %s - 支持模态: %s - 状态: %s",
            assistantId, name, supportedModalities, status);
    }

    /**
     * 是否支持指定模态
     */
    public boolean supportsModality(ModalityType modality) {
        return supportedModalities != null && supportedModalities.contains(modality);
    }

    /**
     * 更新统计信息
     */
    public void updateStats(Long sessionCount, Long messageCount, Double avgResponseTime) {
        if (sessionCount != null) this.totalSessions = sessionCount;
        if (messageCount != null) this.totalMessages = messageCount;
        if (avgResponseTime != null) this.avgResponseTimeMs = avgResponseTime;
        this.lastActiveAt = LocalDateTime.now();
    }

    /**
     * 更新满意度评分
     */
    public void updateSatisfactionScore(Double newScore) {
        if (this.satisfactionScore == null) {
            this.satisfactionScore = newScore;
        } else {
            // 使用移动平均更新评分
            this.satisfactionScore = (this.satisfactionScore * 0.8) + (newScore * 0.2);
        }
    }

    /**
     * 验证配置有效性
     */
    public boolean isValidConfiguration() {
        return assistantId != null && !assistantId.isEmpty()
            && userId != null && !userId.isEmpty()
            && name != null && !name.isEmpty()
            && supportedModalities != null && !supportedModalities.isEmpty()
            && temperature >= 0.0 && temperature <= 2.0;
    }

    @Override
    public String toString() {
        return "MultimodalAIAssistantEntity{" +
            "id=" + id +
            ", assistantId='" + assistantId + '\'' +
            ", name='" + name + '\'' +
            ", status=" + status +
            ", supportedModalities=" + supportedModalities +
            ", totalSessions=" + totalSessions +
            ", totalMessages=" + totalMessages +
            '}';
    }
}
