package com.im.backend.modules.local_life.enums;

import lombok.Getter;

/**
 * 回复类型枚举
 */
@Getter
public enum ReplyType {

    USER(1, "用户回复"),
    MERCHANT(2, "商家回复"),
    PLATFORM(3, "平台官方");

    private final Integer code;
    private final String desc;

    ReplyType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReplyType fromCode(Integer code) {
        if (code == null) return null;
        for (ReplyType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
