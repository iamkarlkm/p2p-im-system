package com.im.backend.model.enums;

/**
 * 签到状态枚举
 */
public enum CheckinStatus {
    VALID("有效签到"),
    INVALID("无效签到"),
    PENDING("待审核"),
    CHEATING("作弊嫌疑"),
    OUT_OF_RANGE("超出范围");

    private final String description;

    CheckinStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
