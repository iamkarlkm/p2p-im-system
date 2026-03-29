package com.im.backend.modules.location.model.enums;

import lombok.Getter;

/**
 * 位置围栏事件类型枚举
 */
@Getter
public enum LocationGeofenceEventType {

    ENTER_DESTINATION("ENTER_DESTINATION", "到达目的地", true),
    LEAVE_DESTINATION("LEAVE_DESTINATION", "离开目的地", true),
    ENTER_SAFE_ZONE("ENTER_SAFE_ZONE", "进入安全区", true),
    LEAVE_SAFE_ZONE("LEAVE_SAFE_ZONE", "离开安全区", true),
    ENTER_SHARED_AREA("ENTER_SHARED_AREA", "进入共享区", false),
    LEAVE_SHARED_AREA("LEAVE_SHARED_AREA", "离开共享区", false),
    ARRIVED("ARRIVED", "已到达", true),
    STILL_MOVING("STILL_MOVING", "移动中", false);

    private final String code;
    private final String description;
    private final Boolean shouldNotify;

    LocationGeofenceEventType(String code, String description, Boolean shouldNotify) {
        this.code = code;
        this.description = description;
        this.shouldNotify = shouldNotify;
    }
}
