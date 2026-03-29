package com.im.backend.modules.geofence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 到店状态
 */
@Getter
@AllArgsConstructor
public enum ArrivalStatus {

    IN_STORE("IN_STORE", "在店中"),
    LEFT("LEFT", "已离店"),
    PROCESSED("PROCESSED", "已处理");

    private final String code;
    private final String desc;

    public static ArrivalStatus fromCode(String code) {
        for (ArrivalStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
