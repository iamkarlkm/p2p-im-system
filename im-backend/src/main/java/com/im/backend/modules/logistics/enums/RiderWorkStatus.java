package com.im.backend.modules.logistics.enums;

import lombok.Getter;

/**
 * 骑手工作状态枚举
 */
@Getter
public enum RiderWorkStatus {

    OFFLINE(0, "离线", "骑手未在线"),
    ONLINE_IDLE(1, "在线空闲", "在线且可以接单"),
    ONLINE_BUSY(2, "在线忙碌", "在线但正在配送中");

    private final int code;
    private final String desc;
    private final String detail;

    RiderWorkStatus(int code, String desc, String detail) {
        this.code = code;
        this.desc = desc;
        this.detail = detail;
    }

    public static RiderWorkStatus getByCode(int code) {
        for (RiderWorkStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
