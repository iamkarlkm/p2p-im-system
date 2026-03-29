package com.im.backend.modules.merchant.review.enums;

import lombok.Getter;

/**
 * 评价维度枚举
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Getter
public enum ReviewDimension {

    /**
     * 综合评分
     */
    OVERALL("overall", "综合评分", "overallRating", "对商户的整体满意度评分"),

    /**
     * 口味评分
     */
    TASTE("taste", "口味", "tasteRating", "餐饮类商户口味评分"),

    /**
     * 环境评分
     */
    ENVIRONMENT("environment", "环境", "environmentRating", "门店环境卫生评分"),

    /**
     * 服务评分
     */
    SERVICE("service", "服务", "serviceRating", "服务态度和效率评分"),

    /**
     * 性价比
     */
    VALUE("value", "性价比", "valueRating", "价格和质量的综合性价比评分"),

    /**
     * 位置便利
     */
    LOCATION("location", "位置便利", "locationRating", "地理位置和交通便利性评分"),

    /**
     * 设施完善
     */
    FACILITY("facility", "设施完善", "facilityRating", "设施设备完善程度评分"),

    /**
     * 卫生状况
     */
    HYGIENE("hygiene", "卫生状况", "hygieneRating", "卫生清洁程度评分");

    private final String code;
    private final String name;
    private final String fieldName;
    private final String description;

    ReviewDimension(String code, String name, String fieldName, String description) {
        this.code = code;
        this.name = name;
        this.fieldName = fieldName;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static ReviewDimension fromCode(String code) {
        if (code == null) return null;
        for (ReviewDimension dimension : values()) {
            if (dimension.code.equals(code)) {
                return dimension;
            }
        }
        return null;
    }

    /**
     * 获取默认维度（综合、口味、环境、服务、性价比）
     */
    public static ReviewDimension[] getDefaultDimensions() {
        return new ReviewDimension[]{
            OVERALL, TASTE, ENVIRONMENT, SERVICE, VALUE
        };
    }

    /**
     * 检查是否为餐饮类专用维度
     */
    public boolean isCateringSpecific() {
        return this == TASTE;
    }

    /**
     * 获取评分显示文本
     */
    public String getRatingText(Integer rating) {
        if (rating == null) return "未评分";
        switch (rating) {
            case 5: return "非常满意";
            case 4: return "满意";
            case 3: return "一般";
            case 2: return "不满意";
            case 1: return "非常不满意";
            default: return "未知";
        }
    }
}
