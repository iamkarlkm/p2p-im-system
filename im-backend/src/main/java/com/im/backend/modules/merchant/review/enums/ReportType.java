package com.im.backend.modules.merchant.review.enums;

/**
 * 举报类型枚举
 */
public enum ReportType {
    FAKE_REVIEW(1, "虚假评价"),
    MALICIOUS_BAD_REVIEW(2, "恶意差评"),
    AD_SPAM(3, "广告垃圾"),
    ILLEGAL(4, "违法违规"),
    OTHER(5, "其他");

    private final int code;
    private final String desc;

    ReportType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
