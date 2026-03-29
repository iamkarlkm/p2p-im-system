package com.im.backend.modules.merchant.assistant.enums;

/**
 * 机器人回复模式枚举
 */
public enum ReplyMode {
    AUTO("AUTO", "全自动回复"),
    SEMI("SEMI", "半自动(辅助人工)"),
    MANUAL("MANUAL", "仅人工");
    
    private final String code;
    private final String desc;
    
    ReplyMode(String code, String desc) {
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
