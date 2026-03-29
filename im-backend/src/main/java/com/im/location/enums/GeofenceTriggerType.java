package com.im.location.enums;

import lombok.Getter;

/**
 * 围栏触发事件类型枚举
 */
@Getter
public enum GeofenceTriggerType {
    ENTER(1, "进入"),
    EXIT(2, "离开"),
    DWELL(3, "停留");
    
    private final Integer code;
    private final String desc;
    
    GeofenceTriggerType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static GeofenceTriggerType fromCode(Integer code) {
        for (GeofenceTriggerType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return ENTER;
    }
}
