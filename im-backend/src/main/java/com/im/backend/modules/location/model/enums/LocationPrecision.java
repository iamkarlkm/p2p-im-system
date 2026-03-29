package com.im.backend.modules.location.model.enums;

import lombok.Getter;

/**
 * 位置精度枚举
 */
@Getter
public enum LocationPrecision {

    HIGH("HIGH", "精确位置", 0),
    AREA("AREA", "商圈级", 500),
    CITY("CITY", "城市级", 5000);

    private final String code;
    private final String description;
    private final Integer blurRadius;

    LocationPrecision(String code, String description, Integer blurRadius) {
        this.code = code;
        this.description = description;
        this.blurRadius = blurRadius;
    }
}
