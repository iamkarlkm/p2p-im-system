package com.im.backend.model.enums;

/**
 * 用户等级枚举
 */
public enum UserLevel {
    BRONZE("青铜", 0, 999, 1.0),
    SILVER("白银", 1000, 4999, 1.2),
    GOLD("黄金", 5000, 19999, 1.5),
    PLATINUM("铂金", 20000, 49999, 1.8),
    DIAMOND("钻石", 50000, 99999, 2.0),
    LEGEND("传奇", 100000, Integer.MAX_VALUE, 2.5);

    private final String name;
    private final int minPoints;
    private final int maxPoints;
    private final double checkinBonus;

    UserLevel(String name, int minPoints, int maxPoints, double checkinBonus) {
        this.name = name;
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.checkinBonus = checkinBonus;
    }

    public String getName() { return name; }
    public int getMinPoints() { return minPoints; }
    public int getMaxPoints() { return maxPoints; }
    public double getCheckinBonus() { return checkinBonus; }

    public static UserLevel fromPoints(int points) {
        for (UserLevel level : values()) {
            if (points >= level.minPoints && points <= level.maxPoints) {
                return level;
            }
        }
        return BRONZE;
    }
}
