package com.im.backend.modules.delivery.model.enums;

import lombok.Getter;

/**
 * 配送任务状态
 */
@Getter
public enum TaskStatus {
    
    PENDING_ASSIGN(0, "待分配"),
    ASSIGNED(1, "已分配"),
    RIDER_ARRIVED(2, "骑手已到店"),
    PICKED_UP(3, "已取货"),
    IN_TRANSIT(4, "配送中"),
    ARRIVED(5, "已送达"),
    COMPLETED(6, "已完成"),
    CANCELLED(7, "已取消"),
    EXCEPTION(8, "异常");
    
    private final int code;
    private final String desc;
    
    TaskStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
