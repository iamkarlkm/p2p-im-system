package com.im.backend.modules.delivery.enums;

import lombok.Getter;

/**
 * 骑手工作状态枚举
 */
@Getter
public enum RiderWorkStatus {
    
    OFFLINE(0, "离线"),
    ONLINE_IDLE(1, "在线空闲"),
    ONLINE_BUSY(2, "在线忙碌"),
    RESTING(3, "休息中"),
    OFF_DUTY(4, "下班");
    
    private final Integer code;
    private final String desc;
    
    RiderWorkStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
