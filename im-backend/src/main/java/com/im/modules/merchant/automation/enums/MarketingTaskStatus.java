package com.im.modules.merchant.automation.enums;

import lombok.Getter;

/**
 * 营销任务状态枚举
 */
@Getter
public enum MarketingTaskStatus {
    
    DRAFT(0, "草稿"),
    ENABLED(1, "已启用"),
    RUNNING(2, "执行中"),
    PAUSED(3, "已暂停"),
    COMPLETED(4, "已完成"),
    DISABLED(5, "已禁用");
    
    private final int code;
    private final String desc;
    
    MarketingTaskStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(int code) {
        for (MarketingTaskStatus status : values()) {
            if (status.code == code) {
                return status.desc;
            }
        }
        return "未知";
    }
}
