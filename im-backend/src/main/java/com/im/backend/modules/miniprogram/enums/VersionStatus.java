package com.im.backend.modules.miniprogram.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 版本状态枚举
 */
@Getter
public enum VersionStatus {

    DEVELOPING("DEVELOPING", "开发中"),
    SUBMITTED("SUBMITTED", "已提交"),
    AUDITING("AUDITING", "审核中"),
    PASSED("PASSED", "审核通过"),
    REJECTED("REJECTED", "审核驳回"),
    RELEASING("RELEASING", "发布中"),
    RELEASED("RELEASED", "已发布"),
    ROLLED_BACK("ROLLED_BACK", "已回滚");

    @EnumValue
    private final String code;
    private final String desc;

    VersionStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
