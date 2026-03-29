package com.im.backend.modules.geofence.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 围栏用途
 */
@Getter
@AllArgsConstructor
public enum GeofencePurpose {

    ARRIVAL("ARRIVAL", "到店检测"),
    DEPARTURE("DEPARTURE", "离店检测"),
    PROMOTION("PROMOTION", "营销推送"),
    DELIVERY("DELIVERY", "配送范围"),
    SERVICE("SERVICE", "服务区域");

    private final String code;
    private final String desc;

    public static GeofencePurpose fromCode(String code) {
        for (GeofencePurpose purpose : values()) {
            if (purpose.code.equals(code)) {
                return purpose;
            }
        }
        return null;
    }
}
