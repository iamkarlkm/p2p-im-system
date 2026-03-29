package com.im.backend.modules.miniprogram.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 沙箱状态枚举
 */
@Getter
public enum SandboxStatus {

    CREATING("CREATING", "创建中"),
    RUNNING("RUNNING", "运行中"),
    PAUSED("PAUSED", "已暂停"),
    STOPPED("STOPPED", "已停止"),
    EXPIRED("EXPIRED", "已过期"),
    ERROR("ERROR", "异常");

    @EnumValue
    private final String code;
    private final String desc;

    SandboxStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
