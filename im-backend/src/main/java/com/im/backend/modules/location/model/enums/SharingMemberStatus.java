package com.im.backend.modules.location.model.enums;

import lombok.Getter;

/**
 * 位置共享成员状态枚举
 */
@Getter
public enum SharingMemberStatus {

    ACTIVE("ACTIVE", "活跃"),
    PAUSED("PAUSED", "已暂停"),
    LEFT("LEFT", "已离开"),
    OFFLINE("OFFLINE", "离线");

    private final String code;
    private final String description;

    SharingMemberStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
