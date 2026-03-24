package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AI 聊天机器人实体
 * 支持多平台 AI 模型集成 (OpenAI/Claude/Gemini/Custom)
 */
@Entity
@Table(name = "im_bot",
       indexes = {
           @Index(name = "idx_bot_owner", columnList = "ownerId"),
           @Index(name = "idx_bot_status", columnList = "status"),
           @Index(name = "idx_bot_type", columnList = "botType"),
           @Index(name = "idx_bot_token", columnList = "accessToken")
       })
public class BotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 机器人UUID */
    @Column(nullable = false, unique = true, length = 36)
    private String botId;

    /** 机器人名称 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 机器人描述 */
    @Column(length = 500)
    private String description;

    /** 机器人头像URL */
    @Column(length = 500)
    private String avatarUrl;

    /** 所属用户ID */
    @Column(nullable = false, length = 36)
    private String ownerId;

    /** 机器人类型: OPENAI/CLAUDE/GEMINI/CUSTOM/LOCAL */
    @Column(nullable = false, length = 20)
    private String botType;

    /** AI 模型名称 */
    @Column(nullable = false, length = 100)
    private String modelName;

    /** API Key (加密存储) */
    @Column(length = 500)
    private String apiKey;

    /** API 基础 URL */
    @Column(length = 500)
    private String apiBaseUrl;

    /** Webhook URL (用于接收消息) */
    @Column(length = 500)
    private String webhookUrl;

    /** Webhook 认证密钥 */
    @Column(length = 200)
    private String webhookSecret;

    /** 系统提示词 */
    @Column(columnDefinition = "TEXT")
    private String systemPrompt;

    /** 最大令牌数 */
    @Column(nullable = false)
    private Integer maxTokens;

    /** Temperature 参数 */
    @Column(nullable = false)
    private Double temperature;

    /** 启用状态 */
    @Column(nullable = false, length = 20)
    private String status;

    /** 是否公开机器人 */
    @Column(nullable = false)
    private Boolean isPublic;

    /** 是否启用 DALL-E 图片生成 */
    @Column(nullable = false)
    private Boolean enableImageGen;

    /** 是否启用语音转文字 */
    @Column(nullable = false)
    private Boolean enableSpeechToText;

    /** 速率限制 (消息/分钟) */
    @Column(nullable = false)
    private Integer rateLimit;

    /** 会话数统计 */
    @Column(nullable = false)
    private Long sessionCount;

    /** 消息数统计 */
    @Column(nullable = false)
    private Long messageCount;

    /** 消耗令牌统计 */
    @Column(nullable = false)
    private Long totalTokensUsed;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    /** 最后活跃时间 */
    private LocalDateTime lastActiveAt;

    /** 访问令牌 (用于 API 调用) */
    @Column(length = 100)
    private String accessToken;

    // ========== Getters and Setters ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBotId() { return botId; }
    public void setBotId(String botId) { this.botId = botId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getBotType() { return botType; }
    public void setBotType(String botType) { this.botType = botType; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getApiBaseUrl() { return apiBaseUrl; }
    public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

    public String getWebhookSecret() { return webhookSecret; }
    public void setWebhookSecret(String webhookSecret) { this.webhookSecret = webhookSecret; }

    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public Boolean getEnableImageGen() { return enableImageGen; }
    public void setEnableImageGen(Boolean enableImageGen) { this.enableImageGen = enableImageGen; }

    public Boolean getEnableSpeechToText() { return enableSpeechToText; }
    public void setEnableSpeechToText(Boolean enableSpeechToText) { this.enableSpeechToText = enableSpeechToText; }

    public Integer getRateLimit() { return rateLimit; }
    public void setRateLimit(Integer rateLimit) { this.rateLimit = rateLimit; }

    public Long getSessionCount() { return sessionCount; }
    public void setSessionCount(Long sessionCount) { this.sessionCount = sessionCount; }

    public Long getMessageCount() { return messageCount; }
    public void setMessageCount(Long messageCount) { this.messageCount = messageCount; }

    public Long getTotalTokensUsed() { return totalTokensUsed; }
    public void setTotalTokensUsed(Long totalTokensUsed) { this.totalTokensUsed = totalTokensUsed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(LocalDateTime lastActiveAt) { this.lastActiveAt = lastActiveAt; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
