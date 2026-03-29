package com.im.backend.modules.merchant.order.enums;

import lombok.Getter;

/**
 * 订单履约会话状态枚举
 */
@Getter
public enum FulfillmentSessionStatus {

    ACTIVE(0, "活跃"),
    PAUSED(1, "暂停"),
    ENDED(2, "已结束"),
    EXPIRED(3, "已过期");

    private final int code;
    private final String desc;

    FulfillmentSessionStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FulfillmentSessionStatus fromCode(int code) {
        for (FulfillmentSessionStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
