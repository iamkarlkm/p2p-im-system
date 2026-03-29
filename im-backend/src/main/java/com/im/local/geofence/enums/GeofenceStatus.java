package com.im.local.geofence.enums;

import lombok.Getter;

/**
 * 地理围栏状态
 */
@Getter
public enum GeofenceStatus {
    
    ACTIVE("有效"),
    INACTIVE("无效"),
    EXPIRED("已过期");
    
    private final String description;
    
    GeofenceStatus(String description) {
        this.description = description;
    }
}
