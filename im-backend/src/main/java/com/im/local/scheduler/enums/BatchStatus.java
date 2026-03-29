package com.im.local.scheduler.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 配送批次状态枚举
 */
@Getter
@AllArgsConstructor
public enum BatchStatus {
    
    PENDING(0, "待分配", "等待分配骑手"),
    ASSIGNED(1, "已分配", "已分配给骑手"),
    PICKING(2, "取餐中", "正在取餐"),
    DELIVERING(3, "配送中", "正在配送"),
    COMPLETED(4, "已完成", "全部送达"),
    CANCELLED(5, "已取消", "批次取消");
    
    private final Integer code;
    private final String name;
    private final String description;
    
    public static BatchStatus fromCode(Integer code) {
        for (BatchStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
