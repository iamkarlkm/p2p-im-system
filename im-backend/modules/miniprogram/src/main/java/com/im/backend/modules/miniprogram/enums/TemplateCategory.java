package com.im.backend.modules.miniprogram.enums;

import lombok.Getter;

/**
 * 模板分类枚举
 */
@Getter
public enum TemplateCategory {

    CATERING(1, "餐饮"),
    RETAIL(2, "零售"),
    SERVICE(3, "服务"),
    ENTERTAINMENT(4, "娱乐"),
    EDUCATION(5, "教育"),
    MEDICAL(6, "医疗"),
    OTHER(7, "其他");

    private final int code;
    private final String desc;

    TemplateCategory(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TemplateCategory fromCode(int code) {
        for (TemplateCategory category : values()) {
            if (category.code == code) {
                return category;
            }
        }
        return OTHER;
    }
}
