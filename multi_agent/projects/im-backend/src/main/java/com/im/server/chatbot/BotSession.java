package com.im.server.chatbot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BotSession {
    private final String sessionId;
    private final String botId;
    private final String userId;
    private final List<BotMessage> messageHistory;
    private final Map<String, Object> context;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;
    private boolean active;

    public BotSession(String sessionId, String botId) {
        this.sessionId = sessionId;
        this.botId = botId;
        this.userId = null;
        this.messageHistory = new ArrayList<>();
        this.context = new ConcurrentHashMap<>();
        this.createdAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
        this.active = true;
    }

    public BotSession(String sessionId, String botId, String userId) {
        this.sessionId = sessionId;
        this.botId = botId;
        this.userId = userId;
        this.messageHistory = new ArrayList<>();
        this.context = new ConcurrentHashMap<>();
        this.createdAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
        this.active = true;
    }

    public void addMessage(BotMessage message) {
        messageHistory.add(message);
        lastActivityAt = LocalDateTime.now();
    }

    public List<BotMessage> getRecentMessages(int count) {
        int size = messageHistory.size();
        if (size <= count) {
            return new ArrayList<>(messageHistory);
        }
        return new ArrayList<>(messageHistory.subList(size - count, size));
    }

    public void setContext(String key, Object value) {
        context.put(key, value);
    }

    public Object getContext(String key) {
        return context.get(key);
    }

    public String getSessionId() { return sessionId; }
    public String getBotId() { return botId; }
    public String getUserId() { return userId; }
    public List<BotMessage> getMessageHistory() { return messageHistory; }
    public Map<String, Object> getContext() { return context; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
