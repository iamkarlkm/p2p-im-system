package com.im.backend.model.poi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 签到成就徽章实体
 * 定义用户可获得的成就徽章
 */
@Entity
@Table(name = "checkin_achievements", indexes = {
    @Index(name = "idx_user_id", columnList = "userId")
})
public class CheckinAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String achievementCode;

    @Column(nullable = false, length = 100)
    private String achievementName;

    @Column(length = 500)
    private String description;

    @Column(length = 200)
    private String iconUrl;

    @Column(nullable = false, length = 20)
    private String rarity;

    @Column(nullable = false)
    private Integer pointsReward = 0;

    @Column(nullable = false)
    private LocalDateTime unlockedAt;

    @Column
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (unlockedAt == null) {
            unlockedAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAchievementCode() { return achievementCode; }
    public void setAchievementCode(String achievementCode) { this.achievementCode = achievementCode; }

    public String getAchievementName() { return achievementName; }
    public void setAchievementName(String achievementName) { this.achievementName = achievementName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    public Integer getPointsReward() { return pointsReward; }
    public void setPointsReward(Integer pointsReward) { this.pointsReward = pointsReward; }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
