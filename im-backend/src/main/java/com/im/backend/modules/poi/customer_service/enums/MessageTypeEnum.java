package com.im.backend.modules.poi.customer_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {
    TEXT("TEXT", "文本消息"),
    IMAGE("IMAGE", "图片消息"),
    VOICE("VOICE", "语音消息"),
    VIDEO("VIDEO", "视频消息"),
    FILE("FILE", "文件消息"),
    LOCATION("LOCATION", "位置消息"),
    PRODUCT("PRODUCT", "商品卡片"),
    ORDER("ORDER", "订单卡片"),
    FAQ("FAQ", "常见问题"),
    SYSTEM("SYSTEM", "系统消息"),
    TYPING("TYPING", "正在输入");

    private final String code;
    private final String desc;

    public static MessageTypeEnum getByCode(String code) {
        for (MessageTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return TEXT;
    }
}
