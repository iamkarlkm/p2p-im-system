package com.im.backend.modules.merchant.review.enums;

/**
 * 评价状态枚举
 */
public enum ReviewStatus {
    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝"),
    DELETED(3, "已删除");

    private final int code;
    private final String desc;

    ReviewStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ReviewStatus fromCode(int code) {
        for (ReviewStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return PENDING;
    }
}
