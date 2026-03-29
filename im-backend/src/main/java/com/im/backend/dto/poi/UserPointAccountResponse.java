package com.im.backend.dto.poi;

import java.time.LocalDateTime;

/**
 * 用户积分账户响应DTO
 */
public class UserPointAccountResponse {
    
    private Long userId;
    private Integer totalPoints;
    private Integer availablePoints;
    private Integer consumedPoints;
    private String currentLevel;
    private String currentLevelName;
    private Integer levelPoints;
    private Integer totalCheckins;
    private Integer consecutiveCheckins;
    private Integer streakDays;
    private Integer maxStreakDays;
    private LocalDateTime lastCheckinTime;
    private Double nextLevelProgress;
    private Integer pointsToNextLevel;

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }

    public Integer getAvailablePoints() { return availablePoints; }
    public void setAvailablePoints(Integer availablePoints) { this.availablePoints = availablePoints; }

    public Integer getConsumedPoints() { return consumedPoints; }
    public void setConsumedPoints(Integer consumedPoints) { this.consumedPoints = consumedPoints; }

    public String getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(String currentLevel) { this.currentLevel = currentLevel; }

    public String getCurrentLevelName() { return currentLevelName; }
    public void setCurrentLevelName(String currentLevelName) { this.currentLevelName = currentLevelName; }

    public Integer getLevelPoints() { return levelPoints; }
    public void setLevelPoints(Integer levelPoints) { this.levelPoints = levelPoints; }

    public Integer getTotalCheckins() { return totalCheckins; }
    public void setTotalCheckins(Integer totalCheckins) { this.totalCheckins = totalCheckins; }

    public Integer getConsecutiveCheckins() { return consecutiveCheckins; }
    public void setConsecutiveCheckins(Integer consecutiveCheckins) { this.consecutiveCheckins = consecutiveCheckins; }

    public Integer getStreakDays() { return streakDays; }
    public void setStreakDays(Integer streakDays) { this.streakDays = streakDays; }

    public Integer getMaxStreakDays() { return maxStreakDays; }
    public void setMaxStreakDays(Integer maxStreakDays) { this.maxStreakDays = maxStreakDays; }

    public LocalDateTime getLastCheckinTime() { return lastCheckinTime; }
    public void setLastCheckinTime(LocalDateTime lastCheckinTime) { this.lastCheckinTime = lastCheckinTime; }

    public Double getNextLevelProgress() { return nextLevelProgress; }
    public void setNextLevelProgress(Double nextLevelProgress) { this.nextLevelProgress = nextLevelProgress; }

    public Integer getPointsToNextLevel() { return pointsToNextLevel; }
    public void setPointsToNextLevel(Integer pointsToNextLevel) { this.pointsToNextLevel = pointsToNextLevel; }
}
