package com.im.backend.modules.logistics.enums;

import lombok.Getter;

/**
 * 配送订单状态枚举
 */
@Getter
public enum DeliveryOrderStatus {

    PENDING(1, "待分配", "订单创建，等待分配骑手"),
    ASSIGNED(2, "已分配", "已分配给骑手，等待接单"),
    ACCEPTED(3, "已取货", "骑手已接单并取货"),
    DELIVERING(4, "配送中", "骑手正在配送"),
    DELIVERED(5, "已送达", "已送达目的地"),
    COMPLETED(6, "已完成", "订单已完成"),
    CANCELLED(7, "已取消", "订单已取消");

    private final int code;
    private final String desc;
    private final String detail;

    DeliveryOrderStatus(int code, String desc, String detail) {
        this.code = code;
        this.desc = desc;
        this.detail = detail;
    }

    public static DeliveryOrderStatus getByCode(int code) {
        for (DeliveryOrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
