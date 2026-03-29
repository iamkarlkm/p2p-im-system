package com.im.backend.model.poi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户积分账户实体
 * 管理用户的积分余额和累计积分
 */
@Entity
@Table(name = "user_point_accounts", indexes = {
    @Index(name = "idx_user_id", columnList = "userId", unique = true),
    @Index(name = "idx_level", columnList = "currentLevel")
})
public class UserPointAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private Integer totalPoints = 0;

    @Column(nullable = false)
    private Integer availablePoints = 0;

    @Column(nullable = false)
    private Integer consumedPoints = 0;

    @Column(nullable = false)
    private Integer frozenPoints = 0;

    @Column(nullable = false, length = 20)
    private String currentLevel = "BRONZE";

    @Column(nullable = false)
    private Integer levelPoints = 0;

    @Column(nullable = false)
    private Integer totalCheckins = 0;

    @Column(nullable = false)
    private Integer consecutiveCheckins = 0;

    @Column
    private LocalDateTime lastCheckinTime;

    @Column
    private Integer totalPoiTypes = 0;

    @Column(nullable = false)
    private Integer streakDays = 0;

    @Column
    private Integer maxStreakDays = 0;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }

    public Integer getAvailablePoints() { return availablePoints; }
    public void setAvailablePoints(Integer availablePoints) { this.availablePoints = availablePoints; }

    public Integer getConsumedPoints() { return consumedPoints; }
    public void setConsumedPoints(Integer consumedPoints) { this.consumedPoints = consumedPoints; }

    public Integer getFrozenPoints() { return frozenPoints; }
    public void setFrozenPoints(Integer frozenPoints) { this.frozenPoints = frozenPoints; }

    public String getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(String currentLevel) { this.currentLevel = currentLevel; }

    public Integer getLevelPoints() { return levelPoints; }
    public void setLevelPoints(Integer levelPoints) { this.levelPoints = levelPoints; }

    public Integer getTotalCheckins() { return totalCheckins; }
    public void setTotalCheckins(Integer totalCheckins) { this.totalCheckins = totalCheckins; }

    public Integer getConsecutiveCheckins() { return consecutiveCheckins; }
    public void setConsecutiveCheckins(Integer consecutiveCheckins) { this.consecutiveCheckins = consecutiveCheckins; }

    public LocalDateTime getLastCheckinTime() { return lastCheckinTime; }
    public void setLastCheckinTime(LocalDateTime lastCheckinTime) { this.lastCheckinTime = lastCheckinTime; }

    public Integer getTotalPoiTypes() { return totalPoiTypes; }
    public void setTotalPoiTypes(Integer totalPoiTypes) { this.totalPoiTypes = totalPoiTypes; }

    public Integer getStreakDays() { return streakDays; }
    public void setStreakDays(Integer streakDays) { this.streakDays = streakDays; }

    public Integer getMaxStreakDays() { return maxStreakDays; }
    public void setMaxStreakDays(Integer maxStreakDays) { this.maxStreakDays = maxStreakDays; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
