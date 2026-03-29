package com.im.location.enums;

import lombok.Getter;

/**
 * 位置共享会话状态枚举
 */
@Getter
public enum SharingSessionStatus {
    CREATED(0, "已创建"),
    ACTIVE(1, "进行中"),
    PAUSED(2, "已暂停"),
    ENDED(3, "已结束");
    
    private final Integer code;
    private final String desc;
    
    SharingSessionStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static SharingSessionStatus fromCode(Integer code) {
        for (SharingSessionStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return CREATED;
    }
}
