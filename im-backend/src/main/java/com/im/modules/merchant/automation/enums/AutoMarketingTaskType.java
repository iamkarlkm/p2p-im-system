package com.im.modules.merchant.automation.enums;

import lombok.Getter;

/**
 * 自动化营销任务类型枚举
 */
@Getter
public enum AutoMarketingTaskType {
    
    NEW_USER_WELCOME(1, "新用户欢迎"),
    SILENT_USER_RECALL(2, "沉默用户召回"),
    ORDER_REVIEW_INVITE(3, "订单评价邀请"),
    ACTIVITY_PROMOTION(4, "活动推广"),
    COUPON_DISTRIBUTION(5, "优惠券发放"),
    BIRTHDAY_BLESSING(6, "生日祝福"),
    SCHEDULED_PUSH(7, "定时推送"),
    BEHAVIOR_TRIGGER(8, "行为触发");
    
    private final int code;
    private final String desc;
    
    AutoMarketingTaskType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(int code) {
        for (AutoMarketingTaskType type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "未知";
    }
}
