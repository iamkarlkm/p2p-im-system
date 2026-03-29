package com.im.backend.dto.poi;

import java.time.LocalDateTime;

/**
 * 成就徽章DTO
 */
public class AchievementDTO {
    
    private String achievementCode;
    private String achievementName;
    private String description;
    private String iconUrl;
    private String rarity;
    private Integer pointsReward;
    private LocalDateTime unlockedAt;

    // Getters and Setters
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
}
