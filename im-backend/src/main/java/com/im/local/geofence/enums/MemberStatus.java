package com.im.local.geofence.enums;

import lombok.Getter;

/**
 * 成员状态
 */
@Getter
public enum MemberStatus {
    
    EN_ROUTE("在路上"),
    NEARBY("附近"),
    ARRIVED("已到达"),
    OFFLINE("离线");
    
    private final String description;
    
    MemberStatus(String description) {
        this.description = description;
    }
}
