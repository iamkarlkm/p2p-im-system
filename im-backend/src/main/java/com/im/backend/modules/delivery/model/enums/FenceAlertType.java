package com.im.backend.modules.delivery.model.enums;

import lombok.Getter;

/**
 * 围栏告警类型
 */
@Getter
public enum FenceAlertType {
    
    DEVIATION(1, "轨迹偏离"),
    STAY_TIMEOUT(2, "停留超时"),
    ENTER_FENCE(3, "进入围栏"),
    EXIT_FENCE(4, "离开围栏"),
    SPEED_OVER(5, "超速告警"),
    OFFLINE(6, "离线告警"),
    IDLE_TIMEOUT(7, "未移动超时"),
    WRONG_DIRECTION(8, "方向错误");
    
    private final int code;
    private final String desc;
    
    FenceAlertType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
