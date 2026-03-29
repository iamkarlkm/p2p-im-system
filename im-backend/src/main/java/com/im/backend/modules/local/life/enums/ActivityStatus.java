package com.im.backend.modules.local.life.enums;

import lombok.Getter;

/**
 * 活动状态枚举
 */
@Getter
public enum ActivityStatus {

    DRAFT("DRAFT", "草稿", "活动创建后的初始状态"),
    PENDING("PENDING", "待审核", "等待平台审核"),
    PUBLISHED("PUBLISHED", "已发布", "活动已发布,可报名"),
    REGISTRATION_CLOSED("REGISTRATION_CLOSED", "报名截止", "报名已截止"),
    ONGOING("ONGOING", "进行中", "活动正在进行中"),
    ENDED("ENDED", "已结束", "活动已结束"),
    CANCELLED("CANCELLED", "已取消", "活动已取消");

    private final String code;
    private final String name;
    private final String description;

    ActivityStatus(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static ActivityStatus fromCode(String code) {
        for (ActivityStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
