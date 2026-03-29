package com.im.backend.modules.merchant.order.enums;

import lombok.Getter;

/**
 * 订单履约消息类型枚举
 */
@Getter
public enum FulfillmentMessageType {

    SYSTEM(1, "系统消息"),
    TEXT(2, "文本消息"),
    IMAGE(3, "图片消息"),
    LOCATION(4, "位置消息"),
    VOICE(5, "语音消息"),
    CARD(6, "卡片消息");

    private final int code;
    private final String desc;

    FulfillmentMessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FulfillmentMessageType fromCode(int code) {
        for (FulfillmentMessageType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
