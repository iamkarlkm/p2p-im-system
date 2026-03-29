package com.im.backend.modules.local_life.enums;

import lombok.Getter;

/**
 * 评价类型枚举
 */
@Getter
public enum ReviewType {

    IMAGE_TEXT(1, "图文评价"),
    VIDEO(2, "视频评价");

    private final Integer code;
    private final String desc;

    ReviewType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReviewType fromCode(Integer code) {
        if (code == null) return null;
        for (ReviewType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
