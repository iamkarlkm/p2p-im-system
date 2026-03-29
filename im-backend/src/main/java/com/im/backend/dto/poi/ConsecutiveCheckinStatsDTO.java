package com.im.backend.dto.poi;

import java.time.LocalDateTime;

/**
 * 连续签到统计DTO
 */
public class ConsecutiveCheckinStatsDTO {
    
    private Integer currentStreak;
    private Integer maxStreak;
    private Integer totalCheckins;
    private LocalDateTime lastCheckinTime;
    private Boolean canCheckinToday;
    private Integer nextRewardDays;
    private Integer nextRewardPoints;

    // Getters and Setters
    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }

    public Integer getMaxStreak() { return maxStreak; }
    public void setMaxStreak(Integer maxStreak) { this.maxStreak = maxStreak; }

    public Integer getTotalCheckins() { return totalCheckins; }
    public void setTotalCheckins(Integer totalCheckins) { this.totalCheckins = totalCheckins; }

    public LocalDateTime getLastCheckinTime() { return lastCheckinTime; }
    public void setLastCheckinTime(LocalDateTime lastCheckinTime) { this.lastCheckinTime = lastCheckinTime; }

    public Boolean getCanCheckinToday() { return canCheckinToday; }
    public void setCanCheckinToday(Boolean canCheckinToday) { this.canCheckinToday = canCheckinToday; }

    public Integer getNextRewardDays() { return nextRewardDays; }
    public void setNextRewardDays(Integer nextRewardDays) { this.nextRewardDays = nextRewardDays; }

    public Integer getNextRewardPoints() { return nextRewardPoints; }
    public void setNextRewardPoints(Integer nextRewardPoints) { this.nextRewardPoints = nextRewardPoints; }
}
