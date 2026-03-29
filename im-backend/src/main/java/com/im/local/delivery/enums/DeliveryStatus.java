package com.im.local.delivery.enums;

import lombok.Getter;

/**
 * 配送订单状态
 */
@Getter
public enum DeliveryStatus {
    
    PENDING_DISPATCH("待派单", "等待系统派单"),
    RIDER_ASSIGNED("已分配", "已分配给骑手"),
    PENDING_PICKUP("待取货", "等待骑手取货"),
    PICKED_UP("已取货", "骑手已取货"),
    DELIVERING("配送中", "正在配送"),
    DELIVERED("已送达", "订单已完成"),
    CANCELLED("已取消", "订单已取消");
    
    private final String description;
    private final String detail;
    
    DeliveryStatus(String description, String detail) {
        this.description = description;
        this.detail = detail;
    }
}
