package com.im.server.bot;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AI聊天机器人实体
 */
public class Bot {
    
    public enum BotType {
        AI_CHAT,     // AI对话机器人
        CUSTOMER,    // 客服机器人
        NOTIFICATION, // 通知机器人
        TOOL         // 工具机器人
    }
    
    public enum BotStatus {
        ACTIVE,      // 活跃
        INACTIVE,    // 未激活
        BANNED       // 被禁用
    }
    
    public enum AIModel {
        OPENAI_GPT4,
        OPENAI_GPT35,
        CLAUDE_3,
        CLAUDE_2,
        WENXIN,      // 文心一言
        TONGYI,      // 通义千问
        SPARK,       // 讯飞星火
        CUSTOM       // 自定义模型
    }
    
    private String botId;
    private String name;
    private String description;
    private String avatar;
    private BotType type;
    private BotStatus status;
    private String ownerId;
    private String ownerName;
    private String groupId;         // 绑定群组，null表示私聊机器人
    private AIModel aiModel;
    private String apiKey;          // 加密存储
    private String modelEndpoint;    // 自定义模型端点
    private double temperature;      // 0.0-2.0
    private int maxTokens;          // 最大回复token
    private String systemPrompt;     // 系统提示词
    private Map<String, String> metadata; // 自定义元数据
    private Set<String> allowedCommands; // 允许的斜杠命令
    private boolean webhooksEnabled;
    private String webhookUrl;
    private String webhookSecret;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long conversationCount;
    private long messageCount;
    
    public Bot() {
        this.botId = UUID.randomUUID().toString();
        this.status = BotStatus.INACTIVE;
        this.type = BotType.AI_CHAT;
        this.aiModel = AIModel.OPENAI_GPT35;
        this.temperature = 0.7;
        this.maxTokens = 2048;
        this.metadata = new HashMap<>();
        this.allowedCommands = new HashSet<>();
        this.webhooksEnabled = false;
        this.createdAt = LocalDateTime.now();
        this.conversationCount = 0;
        this.messageCount = 0;
    }
    
    // Getters and Setters
    public String getBotId() { return botId; }
    public void setBotId(String botId) { this.botId = botId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public BotType getType() { return type; }
    public void setType(BotType type) { this.type = type; }
    
    public BotStatus getStatus() { return status; }
    public void setStatus(BotStatus status) { this.status = status; }
    
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    
    public AIModel getAiModel() { return aiModel; }
    public void setAiModel(AIModel aiModel) { this.aiModel = aiModel; }
    
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    
    public String getModelEndpoint() { return modelEndpoint; }
    public void setModelEndpoint(String modelEndpoint) { this.modelEndpoint = modelEndpoint; }
    
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    
    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    
    public String getSystemPrompt() { return systemPrompt; }
    public void setSystemPrompt(String systemPrompt) { this.systemPrompt = systemPrompt; }
    
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    
    public Set<String> getAllowedCommands() { return allowedCommands; }
    public void setAllowedCommands(Set<String> allowedCommands) { this.allowedCommands = allowedCommands; }
    
    public boolean isWebhooksEnabled() { return webhooksEnabled; }
    public void setWebhooksEnabled(boolean webhooksEnabled) { this.webhooksEnabled = webhooksEnabled; }
    
    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
    
    public String getWebhookSecret() { return webhookSecret; }
    public void setWebhookSecret(String webhookSecret) { this.webhookSecret = webhookSecret; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public long getConversationCount() { return conversationCount; }
    public void setConversationCount(long conversationCount) { this.conversationCount = conversationCount; }
    
    public long getMessageCount() { return messageCount; }
    public void setMessageCount(long messageCount) { this.messageCount = messageCount; }
    
    // 业务方法
    public void activate() { this.status = BotStatus.ACTIVE; }
    public void deactivate() { this.status = BotStatus.INACTIVE; }
    public void ban() { this.status = BotStatus.BANNED; }
    
    public void incrementConversationCount() { this.conversationCount++; }
    public void incrementMessageCount() { this.messageCount++; }
    
    public void addCommand(String command) { this.allowedCommands.add(command); }
    public void removeCommand(String command) { this.allowedCommands.remove(command); }
    public boolean hasCommand(String command) { return this.allowedCommands.contains(command); }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("botId", botId);
        map.put("name", name);
        map.put("description", description);
        map.put("avatar", avatar);
        map.put("type", type.name());
        map.put("status", status.name());
        map.put("ownerId", ownerId);
        map.put("ownerName", ownerName);
        map.put("groupId", groupId);
        map.put("aiModel", aiModel.name());
        map.put("temperature", temperature);
        map.put("maxTokens", maxTokens);
        map.put("systemPrompt", systemPrompt);
        map.put("metadata", metadata);
        map.put("allowedCommands", allowedCommands);
        map.put("webhooksEnabled", webhooksEnabled);
        map.put("createdAt", createdAt != null ? createdAt.toString() : null);
        map.put("updatedAt", updatedAt != null ? updatedAt.toString() : null);
        map.put("conversationCount", conversationCount);
        map.put("messageCount", messageCount);
        return map;
    }
}
