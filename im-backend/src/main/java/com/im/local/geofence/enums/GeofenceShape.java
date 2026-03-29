package com.im.local.geofence.enums;

import lombok.Getter;

/**
 * 地理围栏形状
 */
@Getter
public enum GeofenceShape {
    
    CIRCLE("圆形"),
    POLYGON("多边形");
    
    private final String description;
    
    GeofenceShape(String description) {
        this.description = description;
    }
}
