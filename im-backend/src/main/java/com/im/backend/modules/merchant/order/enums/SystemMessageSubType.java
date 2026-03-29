package com.im.backend.modules.merchant.order.enums;

import lombok.Getter;

/**
 * 系统消息子类型枚举
 */
@Getter
public enum SystemMessageSubType {

    ORDER_CREATED(101, "订单创建"),
    ORDER_ACCEPTED(102, "订单已接单"),
    ORDER_REJECTED(103, "订单被拒绝"),
    MEAL_READY(104, "餐品已出餐"),
    RIDER_ASSIGNED(105, "骑手已分配"),
    RIDER_ARRIVED_MERCHANT(106, "骑手已到店"),
    MEAL_PICKED_UP(107, "骑手已取餐"),
    DELIVERING(108, "开始配送"),
    ARRIVED_USER(109, "已送达"),
    ORDER_COMPLETED(110, "订单完成"),
    DELIVERY_EXCEPTION(111, "配送异常"),
    ORDER_CANCELLED(112, "订单取消"),
    ETA_UPDATED(113, "送达时间更新"),
    LOCATION_SHARED(114, "位置共享");

    private final int code;
    private final String desc;

    SystemMessageSubType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SystemMessageSubType fromCode(int code) {
        for (SystemMessageSubType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
