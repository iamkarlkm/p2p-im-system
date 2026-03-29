package com.im.backend.modules.miniprogram.developer.enums;

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
    OFFLINE(4, "已下线");
    
    private final Integer code;
    private final String desc;
    
    ProjectStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
