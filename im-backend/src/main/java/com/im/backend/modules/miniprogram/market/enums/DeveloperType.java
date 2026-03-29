package com.im.backend.modules.miniprogram.market.enums;

import lombok.Getter;

/**
 * 开发者类型枚举
 */
@Getter
public enum DeveloperType {

    INDIVIDUAL(1, "个人开发者"),
    ENTERPRISE(2, "企业开发者");

    private final Integer code;
    private final String description;

    DeveloperType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DeveloperType fromCode(Integer code) {
        for (DeveloperType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return INDIVIDUAL;
    }
}
