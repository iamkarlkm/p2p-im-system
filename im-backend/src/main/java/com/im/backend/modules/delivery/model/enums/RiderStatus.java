package com.im.backend.modules.delivery.model.enums;

import lombok.Getter;

/**
 * 骑手状态
 */
@Getter
public enum RiderStatus {
    
    OFFLINE(0, "离线"),
    ONLINE_IDLE(1, "在线-空闲"),
    ONLINE_BUSY(2, "在线-忙碌"),
    RESTING(3, "休息中"),
    OFF_DUTY(4, "下班"),
    SUSPENDED(5, "暂停接单"),
    TRAINING(6, "培训中");
    
    private final int code;
    private final String desc;
    
    RiderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
