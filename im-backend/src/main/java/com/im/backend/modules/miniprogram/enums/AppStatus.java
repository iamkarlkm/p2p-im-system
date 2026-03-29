package com.im.backend.modules.miniprogram.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 应用状态枚举
 */
@Getter
public enum AppStatus {

    DEVELOPING("DEVELOPING", "开发中"),
    AUDITING("AUDITING", "审核中"),
    REJECTED("REJECTED", "审核驳回"),
    RELEASED("RELEASED", "已发布"),
    OFFLINE("OFFLINE", "已下架"),
    SUSPENDED("SUSPENDED", "已暂停");

    @EnumValue
    private final String code;
    private final String desc;

    AppStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
