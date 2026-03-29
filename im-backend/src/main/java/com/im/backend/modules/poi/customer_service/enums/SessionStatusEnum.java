package com.im.backend.modules.poi.customer_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会话状态枚举
 */
@Getter
@AllArgsConstructor
public enum SessionStatusEnum {
    PENDING("PENDING", "待分配"),
    ACTIVE("ACTIVE", "进行中"),
    CLOSED("CLOSED", "已关闭"),
    TRANSFERRED("TRANSFERRED", "已转接");

    private final String code;
    private final String desc;

    public static SessionStatusEnum getByCode(String code) {
        for (SessionStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
