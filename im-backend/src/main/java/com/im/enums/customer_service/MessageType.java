package com.im.enums.customer_service;

import lombok.Getter;

/**
 * 消息类型枚举
 * 功能 #319 - 智能客服与工单管理系统
 */
@Getter
public enum MessageType {
    
    TEXT(1, "文本", "纯文本消息"),
    IMAGE(2, "图片", "图片消息"),
    VOICE(3, "语音", "语音消息"),
    EMOJI(4, "表情", "表情消息"),
    CARD(5, "卡片", "富媒体卡片"),
    FILE(6, "文件", "文件消息"),
    SYSTEM(7, "系统", "系统消息");
    
    private final int code;
    private final String name;
    private final String desc;
    
    MessageType(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }
    
    public static MessageType fromCode(int code) {
        for (MessageType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
