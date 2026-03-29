package com.im.backend.modules.miniprogram.enums;

import lombok.Getter;

/**
 * 小程序项目状态枚举
 */
@Getter
public enum ProjectStatus {

    DRAFT(0, "草稿"),
    DEVELOPING(1, "开发中"),
    AUDITING(2, "审核中"),
    PUBLISHED(3, "已发布"),
    OFFLINE(4, "已下架");

    private final int code;
    private final String desc;

    ProjectStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProjectStatus fromCode(int code) {
        for (ProjectStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return DRAFT;
    }
}
