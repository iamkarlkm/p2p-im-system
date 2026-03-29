package com.im.local.geofence.enums;

import lombok.Getter;

/**
 * 地理围栏类型
 */
@Getter
public enum GeofenceType {
    
    STORE("店铺围栏"),
    POI("兴趣点围栏"),
    CUSTOM("自定义围栏"),
    DELIVERY("配送区域"),
    GROUP("群组共享区域");
    
    private final String description;
    
    GeofenceType(String description) {
        this.description = description;
    }
}
