package com.im.location.enums;

import lombok.Getter;

/**
 * 地理围栏类型枚举
 */
@Getter
public enum GeofenceType {
    CIRCLE(1, "圆形"),
    POLYGON(2, "多边形"),
    POLYLINE(3, "线性");
    
    private final Integer code;
    private final String desc;
    
    GeofenceType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static GeofenceType fromCode(Integer code) {
        for (GeofenceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return CIRCLE;
    }
}
