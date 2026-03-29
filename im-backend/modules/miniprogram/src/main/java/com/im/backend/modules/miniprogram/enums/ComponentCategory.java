package com.im.backend.modules.miniprogram.enums;

import lombok.Getter;

/**
 * 组件分类枚举
 */
@Getter
public enum ComponentCategory {

    BASIC(1, "基础组件"),
    LAYOUT(2, "布局组件"),
    FORM(3, "表单组件"),
    DISPLAY(4, "展示组件"),
    BUSINESS(5, "业务组件"),
    MARKETING(6, "营销组件");

    private final int code;
    private final String desc;

    ComponentCategory(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ComponentCategory fromCode(int code) {
        for (ComponentCategory category : values()) {
            if (category.code == code) {
                return category;
            }
        }
        return BASIC;
    }
}
