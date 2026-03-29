package com.im.backend.modules.local_life.enums;

import lombok.Getter;

/**
 * 评价状态枚举
 */
@Getter
public enum ReviewStatus {

    PENDING(0, "待审核"),
    APPROVED(1, "已通过"),
    REJECTED(2, "已拒绝"),
    DELETED(3, "已删除");

    private final Integer code;
    private final String desc;

    ReviewStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReviewStatus fromCode(Integer code) {
        if (code == null) return null;
        for (ReviewStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
