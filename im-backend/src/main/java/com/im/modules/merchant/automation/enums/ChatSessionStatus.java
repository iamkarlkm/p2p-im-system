package com.im.modules.merchant.automation.enums;

import lombok.Getter;

/**
 * 客服会话状态枚举
 */
@Getter
public enum ChatSessionStatus {
    
    INIT(0, "初始化"),
    AI_SERVING(1, "AI服务中"),
    WAITING_TRANSFER(2, "等待转人工"),
    HUMAN_SERVING(3, "人工服务中"),
    RESOLVED(4, "已解决"),
    CLOSED(5, "已关闭"),
    TIMEOUT(6, "已超时");
    
    private final int code;
    private final String desc;
    
    ChatSessionStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(int code) {
        for (ChatSessionStatus status : values()) {
            if (status.code == code) {
                return status.desc;
            }
        }
        return "未知";
    }
}
