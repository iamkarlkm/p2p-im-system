package com.im.system.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 敏感词实体类
 * 用于敏感词库管理
 */
@Entity
@Table(name = "sensitive_words", indexes = {
    @Index(name = "idx_sensitive_word", columnList = "word"),
    @Index(name = "idx_sensitive_category", columnList = "category"),
    @Index(name = "idx_sensitive_level", columnList = "level"),
    @Index(name = "idx_sensitive_enabled", columnList = "enabled"),
    @Index(name = "idx_sensitive_created_at", columnList = "createdAt")
})
public class SensitiveWordEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "word", length = 255, nullable = false, unique = true)
    private String word;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "level", length = 20)
    private String level;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    @Column(name = "replacement", length = 50)
    private String replacement;
    
    @Column(name = "pattern", length = 500)
    private String pattern;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "matchCount", nullable = false)
    private Integer matchCount = 0;
    
    @Column(name = "lastMatchAt")
    private LocalDateTime lastMatchAt;
    
    @Column(name = "createdBy", length = 64)
    private String createdBy;
    
    @Column(name = "updatedBy", length = 64)
    private String updatedBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public SensitiveWordEntity() {}
    
    public SensitiveWordEntity(String word) {
        this.word = word;
    }
    
    // Getter 和 Setter 方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    
    public String getReplacement() { return replacement; }
    public void setReplacement(String replacement) { this.replacement = replacement; }
    
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getMatchCount() { return matchCount; }
    public void setMatchCount(Integer matchCount) { this.matchCount = matchCount; }
    
    public LocalDateTime getLastMatchAt() { return lastMatchAt; }
    public void setLastMatchAt(LocalDateTime lastMatchAt) { this.lastMatchAt = lastMatchAt; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // 工具方法
    public void incrementMatchCount() {
        this.matchCount = (this.matchCount == null ? 1 : this.matchCount + 1);
        this.lastMatchAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "SensitiveWordEntity{" +
               "id=" + id +
               ", word='" + word + '\'' +
               ", category='" + category + '\'' +
               ", level='" + level + '\'' +
               ", enabled=" + enabled +
               ", matchCount=" + matchCount +
               '}';
    }
}