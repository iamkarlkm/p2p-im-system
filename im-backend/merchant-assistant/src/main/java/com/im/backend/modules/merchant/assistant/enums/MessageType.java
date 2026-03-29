package com.im.backend.modules.merchant.assistant.enums;

/**
 * 消息类型枚举
 */
public enum MessageType {
    TEXT("TEXT", "文本消息"),
    IMAGE("IMAGE", "图片消息"),
    VOICE("VOICE", "语音消息"),
    VIDEO("VIDEO", "视频消息"),
    FILE("FILE", "文件消息"),
    TEMPLATE("TEMPLATE", "模板消息"),
    RICH("RICH", "富媒体消息"),
    SYSTEM("SYSTEM", "系统消息");
    
    private final String code;
    private final String desc;
    
    MessageType(String code, String desc) {
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
