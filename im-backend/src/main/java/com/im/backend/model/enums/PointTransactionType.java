package com.im.backend.model.enums;

/**
 * 积分交易类型枚举
 */
public enum PointTransactionType {
    CHECKIN("签到获得"),
    CONSECUTIVE_BONUS("连续签到奖励"),
    FIRST_CHECKIN("首次签到奖励"),
    LEVEL_UP("等级提升奖励"),
    ACHIEVEMENT("成就解锁奖励"),
    ACTIVITY_BONUS("活动奖励"),
    EXCHANGE("积分兑换"),
    CONSUME("积分消费"),
    EXPIRE("积分过期"),
    FROZEN("积分冻结"),
    UNFROZEN("积分解冻"),
    ADJUST("系统调整");

    private final String description;

    PointTransactionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
