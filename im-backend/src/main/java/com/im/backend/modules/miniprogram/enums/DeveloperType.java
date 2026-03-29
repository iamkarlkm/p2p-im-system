package com.im.backend.modules.miniprogram.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 开发者类型枚举
 */
@Getter
public enum DeveloperType {

    INDIVIDUAL("INDIVIDUAL", "个人开发者"),
    ENTERPRISE("ENTERPRISE", "企业开发者");

    @EnumValue
    private final String code;
    private final String desc;

    DeveloperType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
