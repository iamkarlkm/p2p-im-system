package com.im.local.delivery.enums;

/**
 * 配送订单状态枚举
 */
public enum DeliveryOrderStatus {
    
    PENDING_ASSIGN(0, "待分配", "等待系统分配骑手"),
    ASSIGNED(1, "已分配", "已分配骑手，等待取货"),
    PICKING_UP(2, "取货中", "骑手正在前往商家取货"),
    DELIVERING(3, "配送中", "骑手已取货，正在配送"),
    DELIVERED(4, "已送达", "订单已送达完成"),
    EXCEPTION(5, "异常", "配送过程发生异常"),
    CANCELLED(6, "已取消", "订单已取消");
    
    private final int code;
    private final String name;
    private final String desc;
    
    DeliveryOrderStatus(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static DeliveryOrderStatus fromCode(int code) {
        for (DeliveryOrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 是否可进行状态流转
     */
    public boolean canTransitionTo(DeliveryOrderStatus target) {
        if (this == target) return true;
        
        switch (this) {
            case PENDING_ASSIGN:
                return target == ASSIGNED || target == CANCELLED;
            case ASSIGNED:
                return target == PICKING_UP || target == CANCELLED;
            case PICKING_UP:
                return target == DELIVERING || target == EXCEPTION;
            case DELIVERING:
                return target == DELIVERED || target == EXCEPTION;
            case EXCEPTION:
                return target == DELIVERING || target == CANCELLED;
            default:
                return false;
        }
    }
}
