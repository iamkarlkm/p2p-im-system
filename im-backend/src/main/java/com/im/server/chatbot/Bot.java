package com.im.server.chatbot;

import java.time.LocalDateTime;
import java.util.List;

public class Bot {
    private String id;
    private String name;
    private String description;
    private String avatarUrl;
    private String ownerId;
    private String botType; // AI, WEBHOOK, SCRIPTED
    private String aiProvider; // OPENAI, CLAUDE, GEMINI, CUSTOM
    private String aiModel;
    private String webhookUrl;
    private List<String> slashCommands;
    private boolean enabled;
    private boolean globalEnabled;
    private List<String> allowedGroupIds;
    private BotConfig config;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Bot() {
        this.enabled = true;
        this.globalEnabled = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.slashCommands = List.of();
        this.allowedGroupIds = List.of();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getAiProvider() { return aiProvider; }
    public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }

    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }

    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }

    public List<String> getSlashCommands() { return slashCommands; }
    public void setSlashCommands(List<String> slashCommands) { this.slashCommands = slashCommands; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public boolean isGlobalEnabled() { return globalEnabled; }
    public void setGlobalEnabled(boolean globalEnabled) { this.globalEnabled = globalEnabled; }

    public List<String> getAllowedGroupIds() { return allowedGroupIds; }
    public void setAllowedGroupIds(List<String> allowedGroupIds) { this.allowedGroupIds = allowedGroupIds; }

    public BotConfig getConfig() { return config; }
    public void setConfig(BotConfig config) { this.config = config; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
