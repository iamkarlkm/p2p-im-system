package com.im.backend.modules.geofence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 地理围栏类型
 */
@Getter
@AllArgsConstructor
public enum GeofenceType {

    CIRCLE("CIRCLE", "圆形围栏"),
    POLYGON("POLYGON", "多边形围栏"),
    LINE("LINE", "线性围栏");

    private final String code;
    private final String desc;

    public static GeofenceType fromCode(String code) {
        for (GeofenceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
