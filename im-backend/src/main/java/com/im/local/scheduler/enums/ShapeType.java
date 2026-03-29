package com.im.local.scheduler.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 围栏形状类型枚举
 */
@Getter
@AllArgsConstructor
public enum ShapeType {
    
    CIRCLE(1, "圆形", "以圆心半径定义"),
    POLYGON(2, "多边形", "以顶点坐标定义");
    
    private final Integer code;
    private final String name;
    private final String description;
    
    public static ShapeType fromCode(Integer code) {
        for (ShapeType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return CIRCLE;
    }
}
