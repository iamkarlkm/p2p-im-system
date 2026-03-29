package com.im.backend.modules.local.enums;

/**
 * 地理围栏类型枚举
 */
public enum GeofenceType {
    
    CIRCLE(1, "圆形围栏"),
    POLYGON(2, "多边形围栏");
    
    private final Integer code;
    private final String desc;
    
    GeofenceType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static GeofenceType getByCode(Integer code) {
        for (GeofenceType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
