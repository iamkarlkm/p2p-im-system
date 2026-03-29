package com.im.backend.modules.local_life.checkin.enums;

/**
 * 用户等级类型枚举
 */
public enum UserLevel {
    /**
     * 青铜
     */
    BRONZE(1, "青铜", 0, 999, "#CD7F32"),
    
    /**
     * 白银
     */
    SILVER(2, "白银", 1000, 4999, "#C0C0C0"),
    
    /**
     * 黄金
     */
    GOLD(3, "黄金", 5000, 19999, "#FFD700"),
    
    /**
     * 铂金
     */
    PLATINUM(4, "铂金", 20000, 49999, "#E5E4E2"),
    
    /**
     * 钻石
     */
    DIAMOND(5, "钻石", 50000, 99999, "#B9F2FF"),
    
    /**
     * 星耀
     */
    STAR(6, "星耀", 100000, Integer.MAX_VALUE, "#9B59B6");
    
    private final int level;
    private final String name;
    private final int minPoints;
    private final int maxPoints;
    private final String color;
    
    UserLevel(int level, String name, int minPoints, int maxPoints, String color) {
        this.level = level;
        this.name = name;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.color = color;
    }
    
    public static UserLevel getByPoints(int points) {
        for (UserLevel level : values()) {
            if (points >= level.minPoints && points <= level.maxPoints) {
                return level;
            }
        }
        return BRONZE;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getName() {
        return name;
    }
    
    public int getMinPoints() {
        return minPoints;
    }
    
    public int getMaxPoints() {
        return maxPoints;
    }
    
    public String getColor() {
        return color;
    }
}
