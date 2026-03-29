package com.im.modules.merchant.automation.enums;

import lombok.Getter;

/**
 * 提醒记录状态枚举
 */
@Getter
public enum ReminderRecordStatus {
    
    PENDING(0, "待发送"),
    SENDING(1, "发送中"),
    SENT(2, "已发送"),
    READ(3, "已读"),
    FAILED(4, "发送失败");
    
    private final int code;
    private final String desc;
    
    ReminderRecordStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(int code) {
        for (ReminderRecordStatus status : values()) {
            if (status.code == code) {
                return status.desc;
            }
        }
        return "未知";
    }
}
