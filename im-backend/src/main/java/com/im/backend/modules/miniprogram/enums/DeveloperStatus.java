package com.im.backend.modules.miniprogram.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 开发者状态枚举
 */
@Getter
public enum DeveloperStatus {

    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    FROZEN("FROZEN", "已冻结"),
    CANCELLED("CANCELLED", "已注销");

    @EnumValue
    private final String code;
    private final String desc;

    DeveloperStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
