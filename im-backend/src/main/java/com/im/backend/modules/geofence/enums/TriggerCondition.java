package com.im.backend.modules.geofence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 围栏触发条件
 */
@Getter
@AllArgsConstructor
public enum TriggerCondition {

    ENTER("ENTER", "进入围栏"),
    EXIT("EXIT", "离开围栏"),
    DWELL("DWELL", "停留超时");

    private final String code;
    private final String desc;

    public static TriggerCondition fromCode(String code) {
        for (TriggerCondition condition : values()) {
            if (condition.code.equals(code)) {
                return condition;
            }
        }
        return null;
    }
}
