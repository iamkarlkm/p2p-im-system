package com.im.backend.modules.merchant.order.enums;

import lombok.Getter;

/**
 * 签收方式枚举
 */
@Getter
public enum ReceiptType {

    CODE_VERIFICATION(1, "签收码验证"),
    PHOTO_CONFIRMATION(2, "拍照确认"),
    DIRECT_RECEIPT(3, "直接签收"),
    LEFT_AT_DOOR(4, "寄存/放门口");

    private final int code;
    private final String desc;

    ReceiptType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReceiptType fromCode(int code) {
        for (ReceiptType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
