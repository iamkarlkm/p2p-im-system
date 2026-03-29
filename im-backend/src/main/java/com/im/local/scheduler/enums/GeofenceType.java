package com.im.local.scheduler.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 配送围栏类型枚举
 */
@Getter
@AllArgsConstructor
public enum GeofenceType {
    
    BUSINESS(1, "商圈", "商业区域"),
    OFFICE(2, "写字楼", "办公楼宇"),
    RESIDENTIAL(3, "小区", "住宅小区"),
    SCHOOL(4, "学校", "教育机构"),
    HOSPITAL(5, "医院", "医疗机构"),
    CUSTOM(6, "自定义", "自定义区域");
    
    private final Integer code;
    private final String name;
    private final String description;
    
    public static GeofenceType fromCode(Integer code) {
        for (GeofenceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
