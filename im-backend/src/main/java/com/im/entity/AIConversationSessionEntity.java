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
 * AI对话会话实体类
 * 记录用户与AI助手的多轮对话
 */
@Data
@Entity
@Table(name = "ai_conversation_session", indexes = {
    @Index(name = "idx_session_user_id", columnList = "userId"),
    @Index(name = "idx_session_assistant_id", columnList = "assistantId"),
    @Index(name = "idx_session_status", columnList = "status"),
    @Index(name = "idx_session_created_at", columnList = "createdAt"),
    @Index(name = "idx_session_context_id", columnList = "contextId")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AIConversationSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 会话唯一标识
     */
    @Column(name = "session_id", nullable = false, unique = true, length = 64)
    @NotBlank(message = "会话ID不能为空")
    @Size(max = 64, message = "会话ID长度不能超过64")
    private String sessionId;

    /**
     * 上下文追踪ID（用于多轮对话关联）
     */
    @Column(name = "context_id", length = 64)
    private String contextId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, length = 64)
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * AI助手ID
     */
    @Column(name = "assistant_id", nullable = false, length = 64)
    @NotBlank(message = "助手ID不能为空")
    private String assistantId;

    /**
     * 会话标题（自动生成或用户设置）
     */
    @Column(name = "title", length = 256)
    @Size(max = 256, message = "标题长度不能超过256")
    private String title;

    /**
     * 会话状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private SessionStatus status = SessionStatus.ACTIVE;

    /**
     * 当前会话使用的模态类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "current_modality", length = 32)
    private ModalityType currentModality = ModalityType.TEXT;

    /**
     * 消息数量
     */
    @Column(name = "message_count")
    private Integer messageCount = 0;

    /**
     * 用户消息数量
     */
    @Column(name = "user_message_count")
    private Integer userMessageCount = 0;

    /**
     * AI响应数量
     */
    @Column(name = "ai_message_count")
    private Integer aiMessageCount = 0;

    /**
     * 会话总字符数
     */
    @Column(name = "total_characters")
    private Long totalCharacters = 0L;

    /**
     * 会话总token数（估算）
     */
    @Column(name = "total_tokens")
    private Long totalTokens = 0L;

    /**
     * 会话开始时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 最后消息时间
     */
    @UpdateTimestamp
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    /**
     * 会话结束时间
     */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /**
     * 会话持续时间（秒）
     */
    @Column(name = "duration_seconds")
    private Long durationSeconds = 0L;

    /**
     * 会话评分（用户满意度）
     */
    @Column(name = "rating")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;

    /**
     * 用户反馈
     */
    @Column(name = "user_feedback", length = 1024)
    @Size(max = 1024, message = "反馈长度不能超过1024")
    private String userFeedback;

    /**
     * 会话标签（用于分类）
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "session_tags", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "tag", length = 64)
    private List<String> tags;

    /**
     * 是否收藏
     */
    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    /**
     * 是否归档
     */
    @Column(name = "is_archived")
    private Boolean isArchived = false;

    /**
     * 会话摘要（AI生成）
     */
    @Column(name = "session_summary", length = 2048)
    private String sessionSummary;

    /**
     * 关键话题（JSON数组）
     */
    @Column(name = "key_topics", length = 1024)
    private String keyTopics;

    /**
     * 会话元数据（JSON格式）
     */
    @Column(name = "metadata", length = 2048)
    private String metadata;

    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    /**
     * 设备信息
     */
    @Column(name = "device_info", length = 512)
    private String deviceInfo;

    /**
     * 会话状态枚举
     */
    public enum SessionStatus {
        ACTIVE,         // 活跃
        PAUSED,         // 暂停
        ENDED,          // 已结束
        EXPIRED,        // 已过期
        ARCHIVED,       // 已归档
        DELETED         // 已删除
    }

    /**
     * 模态类型枚举
     */
    public enum ModalityType {
        TEXT,           // 文本
        VOICE,          // 语音
        IMAGE,          // 图像
        VIDEO,          // 视频
        MIXED           // 混合
    }

    /**
     * 增加消息计数
     */
    public void incrementMessageCount(boolean isUserMessage, int charCount, int tokenCount) {
        this.messageCount++;
        if (isUserMessage) {
            this.userMessageCount++;
        } else {
            this.aiMessageCount++;
        }
        this.totalCharacters += charCount;
        this.totalTokens += tokenCount;
        this.lastMessageAt = LocalDateTime.now();
    }

    /**
     * 结束会话
     */
    public void endSession() {
        this.status = SessionStatus.ENDED;
        this.endedAt = LocalDateTime.now();
        if (this.createdAt != null) {
            this.durationSeconds = java.time.Duration.between(
                this.createdAt, this.endedAt).getSeconds();
        }
    }

    /**
     * 生成默认标题
     */
    public String generateDefaultTitle() {
        if (this.createdAt != null) {
            return "会话 " + this.createdAt.toLocalDate().toString();
        }
        return "新会话";
    }

    @Override
    public String toString() {
        return "AIConversationSessionEntity{" +
            "id=" + id +
            ", sessionId='" + sessionId + '\'' +
            ", userId='" + userId + '\'' +
            ", assistantId='" + assistantId + '\'' +
            ", title='" + title + '\'' +
            ", status=" + status +
            ", messageCount=" + messageCount +
            '}';
    }
}
