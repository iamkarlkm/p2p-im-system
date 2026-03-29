package com.im.local.review.enums;

import lombok.Getter;

/**
 * 回复者类型枚举
 */
@Getter
public enum ReplierType {

    MERCHANT(1, "商户"),
    USER(2, "用户");

    private final Integer code;
    private final String desc;

    ReplierType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
