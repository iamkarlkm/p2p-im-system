package com.im.backend.dto.poi;

/**
 * 等级规则DTO
 */
public class LevelRuleDTO {
    
    private String levelCode;
    private String levelName;
    private Integer minPoints;
    private Integer maxPoints;
    private Integer levelOrder;
    private String iconUrl;
    private String privileges;
    private Double checkinBonus;

    // Getters and Setters
    public String getLevelCode() { return levelCode; }
    public void setLevelCode(String levelCode) { this.levelCode = levelCode; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }

    public Integer getMinPoints() { return minPoints; }
    public void setMinPoints(Integer minPoints) { this.minPoints = minPoints; }

    public Integer getMaxPoints() { return maxPoints; }
    public void setMaxPoints(Integer maxPoints) { this.maxPoints = maxPoints; }

    public Integer getLevelOrder() { return levelOrder; }
    public void setLevelOrder(Integer levelOrder) { this.levelOrder = levelOrder; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getPrivileges() { return privileges; }
    public void setPrivileges(String privileges) { this.privileges = privileges; }

    public Double getCheckinBonus() { return checkinBonus; }
    public void setCheckinBonus(Double checkinBonus) { this.checkinBonus = checkinBonus; }
}
