package com.im.local.geofence.enums;

import lombok.Getter;

/**
 * 共享状态
 */
@Getter
public enum SharingStatus {
    
    ACTIVE("进行中"),
    ENDED("已结束"),
    EXPIRED("已过期");
    
    private final String description;
    
    SharingStatus(String description) {
        this.description = description;
    }
}
