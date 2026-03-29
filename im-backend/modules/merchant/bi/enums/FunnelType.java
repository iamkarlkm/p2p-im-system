package com.im.backend.modules.merchant.bi.enums;

/**
 * 漏斗类型枚举
 */
public enum FunnelType {

    /** 营销转化漏斗 */
    MARKETING("marketing", "营销转化漏斗"),

    /** 订单转化漏斗 */
    ORDER("order", "订单转化漏斗"),

    /** 访问转化漏斗 */
    VISIT("visit", "访问转化漏斗"),

    /** 新客转化漏斗 */
    NEW_USER("new_user", "新客转化漏斗"),

    /** 会员转化漏斗 */
    MEMBER("member", "会员转化漏斗");

    private final String code;
    private final String desc;

    FunnelType(String code, String desc) {
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
