package com.im.backend.dto.poi;

/**
 * 用户等级信息DTO
 */
public class UserLevelInfoDTO {
    
    private String currentLevel;
    private String currentLevelName;
    private Integer currentPoints;
    private Integer minPoints;
    private Integer maxPoints;
    private Double progress;
    private Integer pointsToNextLevel;
    private String nextLevelName;
    private Double checkinBonus;
    private String privileges;

    // Getters and Setters
    public String getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(String currentLevel) { this.currentLevel = currentLevel; }

    public String getCurrentLevelName() { return currentLevelName; }
    public void setCurrentLevelName(String currentLevelName) { this.currentLevelName = currentLevelName; }

    public Integer getCurrentPoints() { return currentPoints; }
    public void setCurrentPoints(Integer currentPoints) { this.currentPoints = currentPoints; }

    public Integer getMinPoints() { return minPoints; }
    public void setMinPoints(Integer minPoints) { this.minPoints = minPoints; }

    public Integer getMaxPoints() { return maxPoints; }
    public void setMaxPoints(Integer maxPoints) { this.maxPoints = maxPoints; }

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }

    public Integer getPointsToNextLevel() { return pointsToNextLevel; }
    public void setPointsToNextLevel(Integer pointsToNextLevel) { this.pointsToNextLevel = pointsToNextLevel; }

    public String getNextLevelName() { return nextLevelName; }
    public void setNextLevelName(String nextLevelName) { this.nextLevelName = nextLevelName; }

    public Double getCheckinBonus() { return checkinBonus; }
    public void setCheckinBonus(Double checkinBonus) { this.checkinBonus = checkinBonus; }

    public String getPrivileges() { return privileges; }
    public void setPrivileges(String privileges) { this.privileges = privileges; }
}
