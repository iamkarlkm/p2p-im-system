package com.im.backend.modules.local.enums;

/**
 * 调度任务状态枚举
 */
public enum DispatchTaskStatus {
    
    PENDING(0, "待分配"),
    ASSIGNED(1, "已分配"),
    PICKUP(2, "取货中"),
    DELIVERING(3, "配送中"),
    ARRIVED(4, "已到达"),
    COMPLETED(5, "已完成"),
    CANCELLED(6, "已取消"),
    EXCEPTION(7, "异常");
    
    private final Integer code;
    private final String desc;
    
    DispatchTaskStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static DispatchTaskStatus getByCode(Integer code) {
        for (DispatchTaskStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
