package com.im.modules.merchant.automation.enums;

import lombok.Getter;

/**
 * 智能提醒规则类型枚举
 */
@Getter
public enum ReminderRuleType {
    
    ABNORMAL_ORDER(1, "异常订单提醒"),
    LOW_STOCK(2, "库存不足提醒"),
    DAILY_REPORT(3, "经营日报"),
    WEEKLY_REPORT(4, "经营周报"),
    MONTHLY_REPORT(5, "经营月报"),
    NEW_ORDER(6, "新订单提醒"),
    REVIEW_REPLY(7, "评价回复提醒"),
    LOW_RATING(8, "低分评价预警"),
    HIGH_RETURN_RATE(9, "高退单率预警"),
    PERFORMANCE_DROP(10, "业绩下滑预警");
    
    private final int code;
    private final String desc;
    
    ReminderRuleType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(int code) {
        for (ReminderRuleType type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "未知";
    }
}
