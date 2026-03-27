package com.im.backend.model;

import java.time.LocalDateTime;

/**
 * 翻译请求模型
 */
public class TranslationRequest {
    private String id;
    private String text;
    private String sourceLanguage;
    private String targetLanguage;
    private String engine;
    private boolean useCache = true;
    private boolean autoDetect = true;
    private String userId;
    private String sessionId;
    private LocalDateTime timestamp;

    public TranslationRequest() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getSourceLanguage() { return sourceLanguage; }
    public void setSourceLanguage(String sourceLanguage) { this.sourceLanguage = sourceLanguage; }

    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }

    public String getEngine() { return engine; }
    public void setEngine(String engine) { this.engine = engine; }

    public boolean isUseCache() { return useCache; }
    public void setUseCache(boolean useCache) { this.useCache = useCache; }

    public boolean isAutoDetect() { return autoDetect; }
    public void setAutoDetect(boolean autoDetect) { this.autoDetect = autoDetect; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
