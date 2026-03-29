package com.im.backend.modules.location.model.enums;

import lombok.Getter;

/**
 * 位置共享会话状态枚举
 */
@Getter
public enum SharingSessionStatus {

    ACTIVE("ACTIVE", "进行中"),
    PAUSED("PAUSED", "已暂停"),
    ENDED("ENDED", "已结束"),
    EXPIRED("EXPIRED", "已过期");

    private final String code;
    private final String description;

    SharingSessionStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
