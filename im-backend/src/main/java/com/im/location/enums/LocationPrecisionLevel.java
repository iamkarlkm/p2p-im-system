package com.im.location.enums;

import lombok.Getter;

/**
 * 位置精度级别枚举
 */
@Getter
public enum LocationPrecisionLevel {
    EXACT(1, "精确", 10),
    DISTRICT(2, "商圈级", 500),
    CITY(3, "城市级", 5000);
    
    private final Integer code;
    private final String desc;
    private final Integer blurRadius;
    
    LocationPrecisionLevel(Integer code, String desc, Integer blurRadius) {
        this.code = code;
        this.desc = desc;
        this.blurRadius = blurRadius;
    }
    
    public static LocationPrecisionLevel fromCode(Integer code) {
        for (LocationPrecisionLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
        return EXACT;
    }
}
