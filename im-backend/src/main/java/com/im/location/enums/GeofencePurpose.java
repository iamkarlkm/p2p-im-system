package com.im.location.enums;

import lombok.Getter;

/**
 * 围栏用途枚举
 */
@Getter
public enum GeofencePurpose {
    DESTINATION(1, "目的地"),
    SAFE_ZONE(2, "安全区"),
    FORBIDDEN_ZONE(3, "禁入区"),
    REMINDER_POINT(4, "提醒点");
    
    private final Integer code;
    private final String desc;
    
    GeofencePurpose(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static GeofencePurpose fromCode(Integer code) {
        for (GeofencePurpose purpose : values()) {
            if (purpose.code.equals(code)) {
                return purpose;
            }
        }
        return DESTINATION;
    }
}
