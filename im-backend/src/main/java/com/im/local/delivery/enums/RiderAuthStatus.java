package com.im.local.delivery.enums;

/**
 * 骑手认证状态枚举
 */
public enum RiderAuthStatus {
    
    UNAUTHENTICATED(0, "未认证"),
    PENDING(1, "审核中"),
    AUTHENTICATED(2, "已认证"),
    REJECTED(3, "已驳回");
    
    private final int code;
    private final String name;
    
    RiderAuthStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public static RiderAuthStatus fromCode(int code) {
        for (RiderAuthStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
