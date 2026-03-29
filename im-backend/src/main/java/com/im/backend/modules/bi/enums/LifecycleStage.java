package com.im.backend.modules.bi.enums;

import lombok.Getter;

/**
 * 用户生命周期阶段枚举
 */
@Getter
public enum LifecycleStage {

    NEW("new", "新客", "首次消费"),
    GROWING("growing", "成长", "消费频次增加"),
    MATURE("mature", "成熟", "稳定消费"),
    DECLINING("declining", "衰退", "消费频次下降"),
    DORMANT("dormant", "沉睡", "长期未消费"),
    REACTIVATED("reactivated", "回流", "唤醒后再次消费");

    private final String code;
    private final String name;
    private final String description;

    LifecycleStage(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
