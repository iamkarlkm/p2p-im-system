package com.im.backend.modules.miniprogram.enums;

import lombok.Getter;

/**
 * 页面类型枚举
 */
@Getter
public enum PageType {

    HOME(1, "首页"),
    LIST(2, "列表页"),
    DETAIL(3, "详情页"),
    FORM(4, "表单页"),
    CUSTOM(5, "自定义");

    private final int code;
    private final String desc;

    PageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PageType fromCode(int code) {
        for (PageType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return CUSTOM;
    }
}
