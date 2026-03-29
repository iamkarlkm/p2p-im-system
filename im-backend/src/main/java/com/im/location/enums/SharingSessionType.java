package com.im.location.enums;

import lombok.Getter;

/**
 * 位置共享会话类型枚举
 */
@Getter
public enum SharingSessionType {
    FRIEND(1, "好友共享"),
    GROUP(2, "群组共享");
    
    private final Integer code;
    private final String desc;
    
    SharingSessionType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static SharingSessionType fromCode(Integer code) {
        for (SharingSessionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return FRIEND;
    }
}
