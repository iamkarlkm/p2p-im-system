package com.im.location.enums;

import lombok.Getter;

/**
 * 到达状态枚举
 */
@Getter
public enum ArrivalStatus {
    NOT_ARRIVED(0, "未到达"),
    ARRIVED(1, "已到达"),
    NEARBY(2, "附近");
    
    private final Integer code;
    private final String desc;
    
    ArrivalStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static ArrivalStatus fromCode(Integer code) {
        for (ArrivalStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return NOT_ARRIVED;
    }
}
