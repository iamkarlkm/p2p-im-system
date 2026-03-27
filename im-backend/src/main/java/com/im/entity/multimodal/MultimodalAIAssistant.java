package com.im.entity.multimodal;

import com.im.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 多模态AI助手实体
 * 支持文本、语音、图像、视频交互的AI助手
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MultimodalAIAssistant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** AI助手ID */
    private String assistantId;

    /** 助手名称 */
    private String name;

    /** 助手描述 */
    private String description;

    /** 助手类型: CHAT-对话, CODE-编程, IMAGE-图像生成, VIDEO-视频生成, VOICE-语音助手 */
    private AssistantType type;

    /** AI模型名称 */
    private String modelName;

    /** 模型版本 */
    private String modelVersion;

    /** 支持的多模态类型 */
    private List<ModalityType> supportedModalities;

    /** 系统提示词 */
    private String systemPrompt;

    /** 温度参数 (0.0-1.0) */
    private Double temperature;

    /** 最大令牌数 */
    private Integer maxTokens;

    /** 上下文窗口大小 */
    private Integer contextWindow;

    /** 是否启用 */
    private Boolean enabled;

    /** 是否在线 */
    private Boolean online;

    /** 最后活动时间 */
    private LocalDateTime lastActivityTime;

    /** 总对话次数 */
    private Long totalConversations;

    /** 总消息数 */
    private Long totalMessages;

    /** 平均响应时间(ms) */
    private Double avgResponseTime;

    /** 用户评分 (1-5) */
    private Double rating;

    /** 头像URL */
    private String avatarUrl;

    /** 语音ID */
    private String voiceId;

    /** 自定义配置 */
    private Map<String, Object> customConfig;

    /** 创建者ID */
    private Long creatorId;

    /** 可见性: PRIVATE-私有, PUBLIC-公开, WORKSPACE-工作区 */
    private Visibility visibility;

    /**
     * 助手类型枚举
     */
    public enum AssistantType {
        CHAT,           // 对话助手
        CODE,           // 编程助手
        IMAGE,          // 图像生成
        VIDEO,          // 视频生成
        VOICE,          // 语音助手
        ANALYSIS,       // 数据分析
        TRANSLATION,    // 翻译助手
        SUMMARIZATION   // 摘要助手
    }

    /**
     * 多模态类型枚举
     */
    public enum ModalityType {
        TEXT,           // 文本
        IMAGE,          // 图像
        AUDIO,          // 音频
        VIDEO,          // 视频
        DOCUMENT,       // 文档
        CODE            // 代码
    }

    /**
     * 可见性枚举
     */
    public enum Visibility {
        PRIVATE,        // 私有
        PUBLIC,         // 公开
        WORKSPACE       // 工作区
    }

    /**
     * 检查是否支持指定模态
     */
    public boolean supportsModality(ModalityType modality) {
        return supportedModalities != null && supportedModalities.contains(modality);
    }

    /**
     * 更新活动时间
     */
    public void updateActivityTime() {
        this.lastActivityTime = LocalDateTime.now();
    }

    /**
     * 增加对话计数
     */
    public void incrementConversations() {
        this.totalConversations = (this.totalConversations == null ? 0 : this.totalConversations) + 1;
    }

    /**
     * 增加消息计数
     */
    public void incrementMessages() {
        this.totalMessages = (this.totalMessages == null ? 0 : this.totalMessages) + 1;
    }

    /**
     * 更新平均响应时间
     */
    public void updateAverageResponseTime(long newResponseTime) {
        long currentTotal = (long) ((this.avgResponseTime == null ? 0 : this.avgResponseTime) 
            * (this.totalMessages == null ? 0 : this.totalMessages));
        long newTotal = currentTotal + newResponseTime;
        long newCount = (this.totalMessages == null ? 0 : this.totalMessages) + 1;
        this.avgResponseTime = (double) newTotal / newCount;
    }

    @Override
    public String toString() {
        return String.format("MultimodalAIAssistant[id=%s, name=%s, type=%s, model=%s, online=%s]",
            assistantId, name, type, modelName, online);
    }
}
