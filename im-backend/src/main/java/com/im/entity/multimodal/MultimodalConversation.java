package com.im.entity.multimodal;

import com.im.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 多模态对话会话实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MultimodalConversation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 会话ID */
    private String conversationId;

    /** 会话标题 */
    private String title;

    /** 用户ID */
    private Long userId;

    /** AI助手ID */
    private String assistantId;

    /** 会话状态: ACTIVE-活跃, ARCHIVED-已归档, DELETED-已删除 */
    private ConversationStatus status;

    /** 消息数量 */
    private Integer messageCount;

    /** 总令牌使用量 */
    private Long totalTokens;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 最后活动时间 */
    private LocalDateTime lastActivityTime;

    /** 系统提示词 */
    private String systemPrompt;

    /** 上下文消息数量限制 */
    private Integer contextLimit;

    /** 当前使用的模型 */
    private String currentModel;

    /** 会话标签 */
    private List<String> tags;

    /** 是否为收藏 */
    private Boolean starred;

    /** 是否置顶 */
    private Boolean pinned;

    /** 置顶时间 */
    private LocalDateTime pinTime;

    /** 会话摘要 */
    private String summary;

    /** 会话元数据 */
    private ConversationMetadata metadata;

    /**
     * 会话状态枚举
     */
    public enum ConversationStatus {
        ACTIVE,     // 活跃
        ARCHIVED,   // 已归档
        DELETED     // 已删除
    }

    /**
     * 更新活动时间
     */
    public void updateActivityTime() {
        this.lastActivityTime = LocalDateTime.now();
    }

    /**
     * 增加消息计数
     */
    public void incrementMessageCount() {
        this.messageCount = (this.messageCount == null ? 0 : this.messageCount) + 1;
    }

    /**
     * 归档会话
     */
    public void archive() {
        this.status = ConversationStatus.ARCHIVED;
    }

    /**
     * 删除会话
     */
    public void delete() {
        this.status = ConversationStatus.DELETED;
    }

    @Override
    public String toString() {
        return String.format("MultimodalConversation[id=%s, user=%d, assistant=%s, messages=%d]",
            conversationId, userId, assistantId, messageCount);
    }

    /**
     * 会话元数据
     */
    @Data
    public static class ConversationMetadata {
        /** 使用的功能特性 */
        private List<String> features;

        /** 会话主题分类 */
        private String category;

        /** 语言 */
        private String language;

        /** 会话质量评分 */
        private Double qualityScore;

        /** 会话持续时间(分钟) */
        private Long durationMinutes;
    }
}
