package com.im.backend.modules.poi.customer_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客服状态枚举
 */
@Getter
@AllArgsConstructor
public enum CustomerServiceStatusEnum {
    ONLINE("ONLINE", "在线"),
    BUSY("BUSY", "忙碌"),
    AWAY("AWAY", "离开"),
    OFFLINE("OFFLINE", "离线");

    private final String code;
    private final String desc;

    public static CustomerServiceStatusEnum getByCode(String code) {
        for (CustomerServiceStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return OFFLINE;
    }
}
