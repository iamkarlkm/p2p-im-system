package com.im.backend.modules.miniprogram.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * API权限状态枚举
 */
@Getter
public enum ApiPermissionStatus {

    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已拒绝"),
    REVOKED("REVOKED", "已撤销");

    @EnumValue
    private final String code;
    private final String desc;

    ApiPermissionStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
