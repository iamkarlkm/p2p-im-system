package com.im.backend.modules.miniprogram.market.enums;

import lombok.Getter;

/**
 * 场景类型枚举（本地生活场景分类）
 */
@Getter
public enum SceneType {

    CATERING(1, "餐饮美食", "food", "外卖、预订、排队、点餐"),
    LIFESTYLE(2, "生活服务", "service", "家政、维修、洗衣、美容"),
    TRAVEL(3, "出行旅游", "travel", "打车、租车、酒店、景点"),
    SHOPPING(4, "购物零售", "shopping", "商超、便利店、生鲜、母婴"),
    HEALTH(5, "健康医疗", "health", "挂号、问诊、药品、体检"),
    EDUCATION(6, "教育培训", "education", "课程、考试、题库、留学");

    private final Integer code;
    private final String name;
    private final String icon;
    private final String description;

    SceneType(Integer code, String name, String icon, String description) {
        this.code = code;
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public static SceneType fromCode(Integer code) {
        for (SceneType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return CATERING;
    }
}
