package com.im.backend.modules.merchant.review.enums;

/**
 * 口碑榜单类型枚举
 */
public enum ReputationRankType {
    OVERALL("overall", "综合口碑榜"),
    TASTE("taste", "口味榜"),
    SERVICE("service", "服务榜"),
    ENVIRONMENT("environment", "环境榜"),
    VALUE("value", "性价比榜"),
    HOT("hot", "热门榜"),
    RISING("rising", "飙升榜"),
    NEW("new", "新店榜");

    private final String code;
    private final String desc;

    ReputationRankType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
