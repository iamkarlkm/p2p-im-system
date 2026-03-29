package com.im.backend.modules.local_life.checkin.enums;

/**
 * 积分类型枚举
 */
public enum PointType {
    /**
     * 签到积分
     */
    CHECKIN(10, "签到积分", "每日签到获得的基础积分"),
    
    /**
     * 连续签到奖励
     */
    STREAK_BONUS(20, "连续签到奖励", "连续签到额外奖励"),
    
    /**
     * 首次签到
     */
    FIRST_CHECKIN(50, "首次签到", "首次在某POI签到"),
    
    /**
     * 探索达人
     */
    EXPLORER(30, "探索积分", "在新类型POI签到"),
    
    /**
     * 社交互动
     */
    SOCIAL(5, "社交积分", "好友互动获得积分"),
    
    /**
     * 活动奖励
     */
    ACTIVITY(100, "活动奖励", "参与签到活动获得"),
    
    /**
     * 消费抵扣
     */
    REDEEM(-1, "积分兑换", "积分兑换消费");
    
    private final int basePoints;
    private final String name;
    private final String desc;
    
    PointType(int basePoints, String name, String desc) {
        this.basePoints = basePoints;
        this.name = name;
        this.desc = desc;
    }
    
    public int getBasePoints() {
        return basePoints;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDesc() {
        return desc;
    }
}
