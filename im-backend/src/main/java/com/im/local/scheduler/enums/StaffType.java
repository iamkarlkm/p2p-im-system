package com.im.local.scheduler.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 骑手类型枚举
 */
@Getter
@AllArgsConstructor
public enum StaffType {
    
    FULL_TIME(1, "专职骑手", "平台正式员工"),
    PART_TIME(2, "兼职骑手", "灵活用工"),
    CROWDSOURCE(3, "众包骑手", "众包平台接入");
    
    private final Integer code;
    private final String name;
    private final String description;
    
    public static StaffType fromCode(Integer code) {
        for (StaffType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
