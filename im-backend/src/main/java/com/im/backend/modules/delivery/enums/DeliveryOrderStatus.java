package com.im.backend.modules.delivery.enums;

import lombok.Getter;

/**
 * 配送订单状态枚举
 */
@Getter
public enum DeliveryOrderStatus {
    
    PENDING_ASSIGN(0, "待分配"),
    ASSIGNED(1, "已分配"),
    RIDER_ACCEPTED(2, "骑手已接单"),
    ARRIVED_PICKUP(3, "已到达取货点"),
    PICKED_UP(4, "已取货"),
    IN_TRANSIT(5, "配送中"),
    ARRIVED_DELIVERY(6, "已到达配送点"),
    DELIVERED(7, "已送达"),
    COMPLETED(8, "已完成"),
    CANCELLED(9, "已取消"),
    EXCEPTION(10, "异常订单");
    
    private final Integer code;
    private final String desc;
    
    DeliveryOrderStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
