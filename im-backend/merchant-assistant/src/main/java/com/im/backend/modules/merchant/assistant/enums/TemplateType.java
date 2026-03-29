package com.im.backend.modules.merchant.assistant.enums;

/**
 * 模板类型枚举
 */
public enum TemplateType {
    WELCOME("WELCOME", "欢迎消息"),
    REMIND("REMIND", "提醒消息"),
    MARKETING("MARKETING", "营销消息"),
    ORDER("ORDER", "订单消息"),
    DELIVERY("DELIVERY", "配送消息"),
    REVIEW("REVIEW", "评价消息"),
    PROMOTION("PROMOTION", "促销消息"),
    FESTIVAL("FESTIVAL", "节日祝福"),
    BIRTHDAY("BIRTHDAY", "生日祝福"),
    SYSTEM("SYSTEM", "系统消息");
    
    private final String code;
    private final String desc;
    
    TemplateType(String code, String desc) {
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
