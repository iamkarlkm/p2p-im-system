package com.im.local.review.enums;

import lombok.Getter;

/**
 * 评价类型枚举
 */
@Getter
public enum ReviewType {

    TEXT(1, "文字评价"),
    IMAGE(2, "图文评价"),
    VIDEO(3, "视频评价");

    private final Integer code;
    private final String desc;

    ReviewType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReviewType fromCode(Integer code) {
        for (ReviewType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return TEXT;
    }
}
