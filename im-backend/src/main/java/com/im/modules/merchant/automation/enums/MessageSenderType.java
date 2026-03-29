package com.im.modules.merchant.automation.enums;

import lombok.Getter;

/**
 * 消息发送者类型枚举
 */
@Getter
public enum MessageSenderType {
    
    USER(1, "用户"),
    AI_BOT(2, "AI机器人"),
    HUMAN_AGENT(3, "人工客服"),
    SYSTEM(4, "系统");
    
    private final int code;
    private final String desc;
    
    MessageSenderType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(int code) {
        for (MessageSenderType type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "未知";
    }
}
