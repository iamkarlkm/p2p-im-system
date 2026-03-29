package com.im.local.coupon.enums;

/**
 * 积分变动类型枚举
 */
public enum PointsChangeType {

    /** 消费获得 */
    CONSUME_EARN(1, "消费获得", "+"),

    /** 签到获得 */
    SIGNIN_EARN(2, "签到获得", "+"),

    /** 任务获得 */
    TASK_EARN(3, "任务获得", "+"),

    /** 注册赠送 */
    REGISTER_EARN(4, "注册赠送", "+"),

    /** 活动赠送 */
    ACTIVITY_EARN(5, "活动赠送", "+"),

    /** 兑换消耗 */
    REDEEM_DEDUCT(6, "兑换消耗", "-"),

    /** 过期清零 */
    EXPIRE_CLEAR(7, "过期清零", "-"),

    /** 系统调整 */
    SYSTEM_ADJUST(8, "系统调整", "+/-");

    private final Integer code;
    private final String name;
    private final String symbol;

    PointsChangeType(Integer code, String name, String symbol) {
        this.code = code;
        this.name = name;
        this.symbol = symbol;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isEarn() {
        return this.symbol.contains("+");
    }

    public static PointsChangeType getByCode(Integer code) {
        for (PointsChangeType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
