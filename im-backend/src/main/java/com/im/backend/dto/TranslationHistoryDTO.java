package com.im.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 翻译历史DTO
 */
public class TranslationHistoryDTO {
    private String id;
    private String originalText;
    private String translatedText;
    private String sourceLanguage;
    private String targetLanguage;
    private String engineUsed;
    private double confidence;
    private LocalDateTime timestamp;
    private boolean fromCache;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOriginalText() { return originalText; }
    public void setOriginalText(String text) { this.originalText = text; }

    public String getTranslatedText() { return translatedText; }
    public void setTranslatedText(String text) { this.translatedText = text; }

    public String getSourceLanguage() { return sourceLanguage; }
    public void setSourceLanguage(String lang) { this.sourceLanguage = lang; }

    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String lang) { this.targetLanguage = lang; }

    public String getEngineUsed() { return engineUsed; }
    public void setEngineUsed(String engine) { this.engineUsed = engine; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime time) { this.timestamp = time; }

    public boolean isFromCache() { return fromCache; }
    public void setFromCache(boolean fromCache) { this.fromCache = fromCache; }
}
