package com.im.local.geofence.enums;

import lombok.Getter;

/**
 * 地理围栏事件类型
 */
@Getter
public enum GeofenceEventType {
    
    ENTER("进入围栏"),
    EXIT("离开围栏"),
    DWELL("停留满足");
    
    private final String description;
    
    GeofenceEventType(String description) {
        this.description = description;
    }
}
