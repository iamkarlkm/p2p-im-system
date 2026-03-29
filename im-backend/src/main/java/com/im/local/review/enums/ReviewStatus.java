package com.im.local.review.enums;

import lombok.Getter;

/**
 * 评价状态枚举
 */
@Getter
public enum ReviewStatus {

    PENDING_AUDIT(0, "待审核"),
    PUBLISHED(1, "已发布"),
    HIDDEN(2, "已隐藏"),
    DELETED(3, "已删除");

    private final Integer code;
    private final String desc;

    ReviewStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReviewStatus fromCode(Integer code) {
        for (ReviewStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return PENDING_AUDIT;
    }
}
