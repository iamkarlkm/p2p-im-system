package com.im.local.scheduler.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 骑手状态枚举
 */
@Getter
@AllArgsConstructor
public enum StaffStatus {
    
    OFFLINE(0, "离线", "不在线"),
    IDLE(1, "空闲", "可接单"),
    PICKING(2, "取餐中", "前往商家取餐"),
    DELIVERING(3, "配送中", "配送订单中"),
    RESTING(4, "休息中", "暂时休息");
    
    private final Integer code;
    private final String name;
    private final String description;
    
    public static StaffStatus fromCode(Integer code) {
        for (StaffStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    public boolean isAvailable() {
        return this == IDLE;
    }
}
