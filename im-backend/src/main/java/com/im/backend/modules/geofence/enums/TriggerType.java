package com.im.backend.modules.geofence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 触发类型
 */
@Getter
@AllArgsConstructor
public enum TriggerType {

    ENTER("ENTER", "进入围栏"),
    EXIT("EXIT", "离开围栏"),
    DWELL("DWELL", "停留超时");

    private final String code;
    private final String desc;

    public static TriggerType fromCode(String code) {
        for (TriggerType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
