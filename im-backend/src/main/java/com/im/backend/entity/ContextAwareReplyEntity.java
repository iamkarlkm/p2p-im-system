package com.im.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 上下文感知的智能回复生成器实体
 * 存储智能回复生成记录和配置信息
 */
@Entity
@Table(name = "context_aware_reply")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextAwareReplyEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    /**
     * 会话ID（可为空，表示全局回复）
     */
    @Column(name = "session_id")
    private String sessionId;
    
    /**
     * 触发消息ID
     */
    @Column(name = "trigger_message_id")
    private String triggerMessageId;
    
    /**
     * 触发消息内容（截断保留）
     */
    @Column(name = "trigger_message_content", length = 1000)
    private String triggerMessageContent;
    
    /**
     * 上下文摘要（用于识别意图）
     */
    @Column(name = "context_summary", length = 2000)
    private String contextSummary;
    
    /**
     * 识别的意图
     * BUSINESS: 商业/工作, PERSONAL: 个人, SOCIAL: 社交, QUESTION: 提问
     * GREETING: 问候, APPRECIATION: 感谢, EMOTIONAL: 情感表达
     */
    @Column(name = "detected_intent")
    private String detectedIntent;
    
    /**
     * 意图置信度 (0.0 - 1.0)
     */
    @Column(name = "intent_confidence")
    private Double intentConfidence;
    
    /**
     * 生成的回复候选列表（JSON格式存储）
     */
    @Column(name = "reply_candidates", length = 5000)
    private String replyCandidates;
    
    /**
     * 选择的回复（用户选择的）
     */
    @Column(name = "selected_reply", length = 2000)
    private String selectedReply;
    
    /**
     * 推荐的表情符号（JSON数组格式）
     */
    @Column(name = "recommended_emojis", length = 500)
    private String recommendedEmojis;
    
    /**
     * 语言风格
     * FORMAL: 正式, CASUAL: 随意, FRIENDLY: 友好, PROFESSIONAL: 专业
     */
    @Column(name = "language_style")
    private String languageStyle;
    
    /**
     * 回复长度
     * SHORT: 简短, MEDIUM: 中等, LONG: 详细
     */
    @Column(name = "reply_length")
    private String replyLength;
    
    /**
     * 敏感性检查结果
     */
    @Column(name = "sensitivity_check_result", length = 500)
    private String sensitivityCheckResult;
    
    /**
     * 是否通过敏感性检查
     */
    @Column(name = "sensitivity_passed")
    private Boolean sensitivityPassed;
    
    /**
     * 个性化特征（JSON格式存储用户偏好）
     */
    @Column(name = "personalization_features", length = 2000)
    private String personalizationFeatures;
    
    /**
     * 用户反馈评分 (1-5)
     */
    @Column(name = "user_feedback_score")
    private Integer userFeedbackScore;
    
    /**
     * 用户反馈评论
     */
    @Column(name = "user_feedback_comment", length = 500)
    private String userFeedbackComment;
    
    /**
     * 是否使用了该回复
     */
    @Column(name = "used")
    private Boolean used;
    
    /**
     * 生成时间（毫秒）
     */
    @Column(name = "generation_time_ms")
    private Long generationTimeMs;
    
    /**
     * 模型版本
     */
    @Column(name = "model_version")
    private String modelVersion;
    
    /**
     * 生成选项（JSON格式存储生成参数）
     */
    @Column(name = "generation_options", length = 1000)
    private String generationOptions;
    
    /**
     * 状态
     * GENERATED: 已生成, SELECTED: 已选择, REJECTED: 已拒绝, EXPIRED: 已过期
     */
    @Column(name = "status", nullable = false)
    private String status;
    
    /**
     * 过期时间
     */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
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
     * 索引字段（用于快速查询）
     */
    @Column(name = "index_key")
    private String indexKey;
    
    /**
     * 意图检测器枚举
     */
    public static class Intent {
        public static final String BUSINESS = "BUSINESS";
        public static final String PERSONAL = "PERSONAL";
        public static final String SOCIAL = "SOCIAL";
        public static final String QUESTION = "QUESTION";
        public static final String GREETING = "GREETING";
        public static final String APPRECIATION = "APPRECIATION";
        public static final String EMOTIONAL = "EMOTIONAL";
        
        public static final String[] ALL_INTENTS = {
            BUSINESS, PERSONAL, SOCIAL, QUESTION, 
            GREETING, APPRECIATION, EMOTIONAL
        };
    }
    
    /**
     * 语言风格枚举
     */
    public static class LanguageStyle {
        public static final String FORMAL = "FORMAL";
        public static final String CASUAL = "CASUAL";
        public static final String FRIENDLY = "FRIENDLY";
        public static final String PROFESSIONAL = "PROFESSIONAL";
        
        public static final String[] ALL_STYLES = {
            FORMAL, CASUAL, FRIENDLY, PROFESSIONAL
        };
    }
    
    /**
     * 回复长度枚举
     */
    public static class ReplyLength {
        public static final String SHORT = "SHORT";
        public static final String MEDIUM = "MEDIUM";
        public static final String LONG = "LONG";
        
        public static final String[] ALL_LENGTHS = {
            SHORT, MEDIUM, LONG
        };
    }
    
    /**
     * 状态枚举
     */
    public static class Status {
        public static final String GENERATED = "GENERATED";
        public static final String SELECTED = "SELECTED";
        public static final String REJECTED = "REJECTED";
        public static final String EXPIRED = "EXPIRED";
        
        public static final String[] ALL_STATUSES = {
            GENERATED, SELECTED, REJECTED, EXPIRED
        };
    }
    
    /**
     * 生成完整的索引键
     */
    public String generateIndexKey() {
        return userId + "_" + (sessionId != null ? sessionId : "global") + "_" + status;
    }
    
    /**
     * 判断回复是否已过期
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * 判断是否为高质量的回复（基于反馈评分）
     */
    public boolean isHighQuality() {
        return userFeedbackScore != null && userFeedbackScore >= 4;
    }
}