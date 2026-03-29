package com.im.backend.modules.merchant.assistant.enums;

/**
 * 发送者类型枚举
 */
public enum SenderType {
    USER("USER", "用户"),
    BOT("BOT", "机器人"),
    AGENT("AGENT", "人工客服"),
    SYSTEM("SYSTEM", "系统");
    
    private final String code;
    private final String desc;
    
    SenderType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}
