package com.im.backend.modules.miniprogram.market.enums;

import lombok.Getter;

/**
 * 小程序状态枚举
 */
@Getter
public enum MiniProgramStatus {

    PENDING_AUDIT(0, "待审核"),
    PUBLISHED(1, "已上架"),
    OFFLINE(2, "已下架"),
    AUDIT_REJECTED(3, "审核拒绝");

    private final Integer code;
    private final String description;

    MiniProgramStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static MiniProgramStatus fromCode(Integer code) {
        for (MiniProgramStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return PENDING_AUDIT;
    }
}
