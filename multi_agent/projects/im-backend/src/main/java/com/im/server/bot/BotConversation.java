package com.im.server.bot;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 机器人会话实体
 */
public class BotConversation {
    
    private String conversationId;
    private String botId;
    private String userId;
    private String sessionId;
    private List<ChatMessage> messages;
    private Map<String, Object> context;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private long messageCount;
    private boolean active;
    
    public BotConversation() {
        this.conversationId = UUID.randomUUID().toString();
        this.messages = new ArrayList<>();
        this.context = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.lastMessageAt = LocalDateTime.now();
        this.messageCount = 0;
        this.active = true;
    }
    
    public static class ChatMessage {
        private String messageId;
        private String role;      // user / assistant / system
        private String content;
        private LocalDateTime timestamp;
        
        public ChatMessage() {}
        
        public ChatMessage(String role, String content) {
            this.messageId = UUID.randomUUID().toString();
            this.role = role;
            this.content = content;
            this.timestamp = LocalDateTime.now();
        }
        
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    // Getters and Setters
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    public String getBotId() { return botId; }
    public void setBotId(String botId) { this.botId = botId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    public long getMessageCount() { return messageCount; }
    public void setMessageCount(long messageCount) { this.messageCount = messageCount; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    // 业务方法
    public void addUserMessage(String content) {
        this.messages.add(new ChatMessage("user", content));
        this.lastMessageAt = LocalDateTime.now();
        this.messageCount++;
    }
    
    public void addAssistantMessage(String content) {
        this.messages.add(new ChatMessage("assistant", content));
        this.lastMessageAt = LocalDateTime.now();
        this.messageCount++;
    }
    
    public void setContextValue(String key, Object value) {
        this.context.put(key, value);
    }
    
    public Object getContextValue(String key) {
        return this.context.get(key);
    }
    
    public List<Map<String, Object>> toOpenAIMessages() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatMessage msg : messages) {
            Map<String, Object> m = new HashMap<>();
            m.put("role", msg.getRole());
            m.put("content", msg.getContent());
            result.add(m);
        }
        return result;
    }
}
