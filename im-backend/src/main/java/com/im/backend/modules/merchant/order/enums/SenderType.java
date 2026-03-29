package com.im.backend.modules.merchant.order.enums;

import lombok.Getter;

/**
 * 发送者类型枚举
 */
@Getter
public enum SenderType {

    USER(1, "用户"),
    MERCHANT(2, "商户"),
    RIDER(3, "骑手"),
    SYSTEM(4, "系统");

    private final int code;
    private final String desc;

    SenderType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SenderType fromCode(int code) {
        for (SenderType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
