package com.im.backend.modules.merchant.assistant.enums;

/**
 * 营销规则类型枚举
 */
public enum MarketingRuleType {
    GEO_FENCE("GEO_FENCE", "地理围栏触发"),
    BEHAVIOR("BEHAVIOR", "行为触发"),
    TIME("TIME", "定时触发"),
    EVENT("EVENT", "事件触发"),
    BIRTHDAY("BIRTHDAY", "生日触发"),
    SILENT("SILENT", "沉默唤醒"),
    FIRST_ORDER("FIRST_ORDER", "首单引导"),
    REVIEW_INVITE("REVIEW_INVITE", "评价邀请");
    
    private final String code;
    private final String desc;
    
    MarketingRuleType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}
