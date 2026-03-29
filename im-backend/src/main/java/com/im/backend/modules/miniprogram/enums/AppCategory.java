package com.im.backend.modules.miniprogram.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 应用分类枚举
 */
@Getter
public enum AppCategory {

    FOOD("FOOD", "餐饮美食"),
    RETAIL("RETAIL", "零售购物"),
    BEAUTY("BEAUTY", "美妆护肤"),
    ENTERTAINMENT("ENTERTAINMENT", "休闲娱乐"),
    EDUCATION("EDUCATION", "教育培训"),
    HEALTH("HEALTH", "医疗健康"),
    TRAVEL("TRAVEL", "旅游出行"),
    LIFE("LIFE", "生活服务"),
    SOCIAL("SOCIAL", "社交互动"),
    TOOLS("TOOLS", "工具效率"),
    FINANCE("FINANCE", "金融理财"),
    GOVERNMENT("GOVERNMENT", "政务民生");

    @EnumValue
    private final String code;
    private final String desc;

    AppCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
