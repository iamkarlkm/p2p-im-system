package com.im.entity.multimodal;

import com.im.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 多模态消息实体
 * 支持多种模态内容的消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MultimodalMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private String messageId;

    /** 会话ID */
    private String conversationId;

    /** 发送者ID */
    private Long senderId;

    /** 发送者类型: USER-用户, ASSISTANT-AI助手, SYSTEM-系统 */
    private SenderType senderType;

    /** AI助手ID (如果是助手发送的消息) */
    private String assistantId;

    /** 消息角色: user, assistant, system */
    private MessageRole role;

    /** 消息内容 - 文本部分 */
    private String content;

    /** 消息模态类型 */
    private MultimodalAIAssistant.ModalityType modalityType;

    /** 附件列表 */
    private List<MessageAttachment> attachments;

    /** 引用的消息ID */
    private String replyToMessageId;

    /** 消息状态: PENDING-待处理, PROCESSING-处理中, COMPLETED-完成, FAILED-失败 */
    private MessageStatus status;

    /** 处理进度 (0-100) */
    private Integer processingProgress;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 完成时间 */
    private LocalDateTime completeTime;

    /** 令牌使用量 */
    private TokenUsage tokenUsage;

    /** 处理耗时(ms) */
    private Long processingTime;

    /** 模型名称 */
    private String modelName;

    /** 模型版本 */
    private String modelVersion;

    /** 是否已编辑 */
    private Boolean edited;

    /** 编辑时间 */
    private LocalDateTime editTime;

    /** 编辑次数 */
    private Integer editCount;

    /** 反馈评分 (1-5) */
    private Integer feedbackRating;

    /** 反馈内容 */
    private String feedbackContent;

    /** 元数据 */
    private MessageMetadata metadata;

    /**
     * 发送者类型枚举
     */
    public enum SenderType {
        USER,       // 用户
        ASSISTANT,  // AI助手
        SYSTEM      // 系统
    }

    /**
     * 消息角色枚举
     */
    public enum MessageRole {
        user,       // 用户
        assistant,  // 助手
        system      // 系统
    }

    /**
     * 消息状态枚举
     */
    public enum MessageStatus {
        PENDING,        // 待处理
        PROCESSING,     // 处理中
        STREAMING,      // 流式输出中
        COMPLETED,      // 完成
        FAILED,         // 失败
        CANCELLED,      // 已取消
        RATE_LIMITED    // 速率限制
    }

    /**
     * 添加附件
     */
    public void addAttachment(MessageAttachment attachment) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(attachment);
    }

    /**
     * 更新为流式状态
     */
    public void startStreaming() {
        this.status = MessageStatus.STREAMING;
    }

    /**
     * 标记为完成
     */
    public void markCompleted() {
        this.status = MessageStatus.COMPLETED;
        this.completeTime = LocalDateTime.now();
    }

    /**
     * 标记为失败
     */
    public void markFailed() {
        this.status = MessageStatus.FAILED;
        this.completeTime = LocalDateTime.now();
    }

    /**
     * 编辑消息
     */
    public void edit(String newContent) {
        this.content = newContent;
        this.edited = true;
        this.editTime = LocalDateTime.now();
        this.editCount = (this.editCount == null ? 0 : this.editCount) + 1;
    }

    /**
     * 添加反馈
     */
    public void addFeedback(int rating, String feedback) {
        this.feedbackRating = rating;
        this.feedbackContent = feedback;
    }

    @Override
    public String toString() {
        return String.format("MultimodalMessage[id=%s, conversation=%s, role=%s, status=%s, modality=%s]",
            messageId, conversationId, role, status, modalityType);
    }

    /**
     * 令牌使用量内部类
     */
    @Data
    public static class TokenUsage {
        /** 提示令牌数 */
        private Integer promptTokens;

        /** 完成令牌数 */
        private Integer completionTokens;

        /** 总令牌数 */
        private Integer totalTokens;

        public TokenUsage() {}

        public TokenUsage(int promptTokens, int completionTokens) {
            this.promptTokens = promptTokens;
            this.completionTokens = completionTokens;
            this.totalTokens = promptTokens + completionTokens;
        }
    }

    /**
     * 消息元数据
     */
    @Data
    public static class MessageMetadata {
        /** 是否包含敏感内容 */
        private Boolean containsSensitiveContent;

        /** 内容安全检查结果 */
        private String safetyCheckResult;

        /** 语言检测 */
        private String detectedLanguage;

        /** 情绪分析 */
        private String sentiment;

        /** 置信度分数 */
        private Double confidenceScore;

        /** 额外元数据 */
        private java.util.Map<String, Object> extra;
    }
}
