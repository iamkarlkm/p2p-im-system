package com.im.backend.modules.local_life.checkin.enums;

/**
 * 成就徽章类型枚举
 */
public enum BadgeType {
    /**
     * 首次签到
     */
    FIRST_CHECKIN("first_checkin", "初来乍到", "完成首次签到", 10, "/badges/first.png"),
    
    /**
     * 连续7天签到
     */
    STREAK_7("streak_7", "持之以恒", "连续签到7天", 50, "/badges/streak7.png"),
    
    /**
     * 连续30天签到
     */
    STREAK_30("streak_30", "签到达人", "连续签到30天", 200, "/badges/streak30.png"),
    
    /**
     * 连续365天签到
     */
    STREAK_365("streak_365", "签到传奇", "连续签到365天", 2000, "/badges/streak365.png"),
    
    /**
     * 探索10个不同类型POI
     */
    EXPLORER_10("explorer_10", "探索者", "在10个不同类型POI签到", 100, "/badges/explorer10.png"),
    
    /**
     * 探索50个不同类型POI
     */
    EXPLORER_50("explorer_50", "探险家", "在50个不同类型POI签到", 500, "/badges/explorer50.png"),
    
    /**
     * 社交达人
     */
    SOCIAL_STAR("social_star", "社交之星", "签到动态获得100个赞", 300, "/badges/social.png"),
    
    /**
     * 凌晨签到
     */
    NIGHT_OWL("night_owl", "夜猫子", "在凌晨0-5点签到", 30, "/badges/night.png"),
    
    /**
     * 早起鸟
     */
    EARLY_BIRD("early_bird", "早起鸟", "在早上5-7点签到", 30, "/badges/morning.png");
    
    private final String code;
    private final String name;
    private final String desc;
    private final int bonusPoints;
    private final String iconUrl;
    
    BadgeType(String code, String name, String desc, int bonusPoints, String iconUrl) {
        this.code = code;
        this.name = name;
        this.desc = desc;
        this.bonusPoints = bonusPoints;
        this.iconUrl = iconUrl;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public int getBonusPoints() {
        return bonusPoints;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
}
