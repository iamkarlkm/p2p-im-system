package com.im.backend.modules.merchant.review.enums;

/**
 * 回复者类型枚举
 */
public enum ReplierType {
    MERCHANT(1, "商户"),
    USER(2, "用户");

    private final int code;
    private final String desc;

    ReplierType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
