package com.im.backend.modules.delivery.enums;

import lombok.Getter;

/**
 * 骑手认证状态枚举
 */
@Getter
public enum RiderAuthStatus {
    
    PENDING(0, "待认证"),
    REVIEWING(1, "审核中"),
    APPROVED(2, "已认证"),
    REJECTED(3, "认证失败"),
    DISABLED(4, "已禁用");
    
    private final Integer code;
    private final String desc;
    
    RiderAuthStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
