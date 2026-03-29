package com.im.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

/**
 * 翻译缓存实体
 */
@Entity
@Table(name = "translation_cache")
public class TranslationCache {
    
    @Id
    private String id;
    
    @Column(name = "cache_key", unique = true, nullable = false)
    private String cacheKey;
    
    @Column(name = "original_text", length = 5000)
    private String originalText;
    
    @Column(name = "translated_text", length = 5000)
    private String translatedText;
    
    @Column(name = "source_language")
    private String sourceLanguage;
    
    @Column(name = "target_language")
    private String targetLanguage;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "hit_count")
    private int hitCount = 0;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCacheKey() { return cacheKey; }
    public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }

    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }

    public String getTranslatedText() { return translatedText; }
    public void setTranslatedText(String translatedText) { this.translatedText = translatedText; }

    public String getSourceLanguage() { return sourceLanguage; }
    public void setSourceLanguage(String sourceLanguage) { this.sourceLanguage = sourceLanguage; }

    public String getTargetLanguage() { return targetLanguage; }
    public void setTargetLanguage(String targetLanguage) { this.targetLanguage = targetLanguage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public int getHitCount() { return hitCount; }
    public void setHitCount(int hitCount) { this.hitCount = hitCount; }
    
    public void incrementHitCount() { this.hitCount++; }
}
