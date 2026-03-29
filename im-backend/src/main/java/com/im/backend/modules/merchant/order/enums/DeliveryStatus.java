package com.im.backend.modules.merchant.order.enums;

import lombok.Getter;

/**
 * 订单配送状态枚举
 */
@Getter
public enum DeliveryStatus {

    WAITING_ACCEPT(0, "待接单"),
    ACCEPTED(1, "已接单"),
    ARRIVED_MERCHANT(2, "已到店"),
    MEAL_PICKED_UP(3, "已取餐"),
    DELIVERING(4, "配送中"),
    ARRIVED_USER(5, "已送达"),
    COMPLETED(6, "已完成"),
    EXCEPTION(7, "配送异常"),
    CANCELLED(8, "已取消");

    private final int code;
    private final String desc;

    DeliveryStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DeliveryStatus fromCode(int code) {
        for (DeliveryStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
