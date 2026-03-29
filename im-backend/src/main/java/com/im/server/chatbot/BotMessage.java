package com.im.server.chatbot;

import java.time.LocalDateTime;
import java.util.Map;

public class BotMessage {
    private String id;
    private String botId;
    private String senderId;
    private String content;
    private String messageType;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;

    public BotMessage() {
        this.timestamp = LocalDateTime.now();
        this.messageType = "text";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBotId() { return botId; }
    public void setBotId(String botId) { this.botId = botId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
