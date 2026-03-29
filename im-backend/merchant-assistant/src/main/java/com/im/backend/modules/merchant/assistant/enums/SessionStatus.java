package com.im.backend.modules.merchant.assistant.enums;

/**
 * 会话状态枚举
 */
public enum SessionStatus {
    INIT("INIT", "初始化"),
    BOT("BOT", "机器人服务中"),
    QUEUE("QUEUE", "排队等待人工"),
    AGENT("AGENT", "人工服务中"),
    ENDED("ENDED", "已结束");
    
    private final String code;
    private final String desc;
    
    SessionStatus(String code, String desc) {
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
