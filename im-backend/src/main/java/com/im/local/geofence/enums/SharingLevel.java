package com.im.local.geofence.enums;

import lombok.Getter;

/**
 * 共享级别
 */
@Getter
public enum SharingLevel {
    
    PRECISE("精确位置"),
    APPROXIMATE("大致位置"),
    REGION("区域级");
    
    private final String description;
    
    SharingLevel(String description) {
        this.description = description;
    }
}
