package com.im.local.delivery.enums;

/**
 * 骑手状态枚举
 */
public enum RiderStatus {
    
    OFFLINE(0, "离线", "骑手当前不在线"),
    IDLE(1, "空闲", "在线等待接单"),
    ACCEPTING(2, "接单中", "已接单，准备取货"),
    PICKING_UP(3, "取货中", "正在前往商家取货"),
    DELIVERING(4, "配送中", "已取货，正在配送");
    
    private final int code;
    private final String name;
    private final String desc;
    
    RiderStatus(int code, String name, String desc) {
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
    
    public static RiderStatus fromCode(int code) {
        for (RiderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 是否可接单
     */
    public boolean canAcceptOrder() {
        return this == IDLE;
    }
}
