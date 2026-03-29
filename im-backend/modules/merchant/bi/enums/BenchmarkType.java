package com.im.backend.modules.merchant.bi.enums;

/**
 * 对标类型枚举
 */
public enum BenchmarkType {

    /** 商圈对标 */
    DISTRICT("district", "商圈对标"),

    /** 品类对标 */
    CATEGORY("category", "品类对标"),

    /** 头部商户对标 */
    TOP_MERCHANT("top_merchant", "头部商户对标"),

    /** 同城对标 */
    CITY("city", "同城对标"),

    /** 全国对标 */
    NATIONWIDE("nationwide", "全国对标");

    private final String code;
    private final String desc;

    BenchmarkType(String code, String desc) {
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
