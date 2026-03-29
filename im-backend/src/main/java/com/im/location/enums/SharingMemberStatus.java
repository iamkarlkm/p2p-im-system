package com.im.location.enums;

import lombok.Getter;

/**
 * 位置共享成员状态枚举
 */
@Getter
public enum SharingMemberStatus {
    PENDING(0, "待加入"),
    JOINED(1, "已加入"),
    LEFT(2, "已离开"),
    REMOVED(3, "已移除");
    
    private final Integer code;
    private final String desc;
    
    SharingMemberStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static SharingMemberStatus fromCode(Integer code) {
        for (SharingMemberStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return PENDING;
    }
}
