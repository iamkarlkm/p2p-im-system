package com.im.backend.modules.local.life.enums;

import lombok.Getter;

/**
 * 支付方式枚举
 */
@Getter
public enum PaymentType {

    FREE("FREE", "免费", "活动完全免费参加"),
    AA("AA", "AA制", "参与者均摊费用"),
    PAID("PAID", "付费", "组织者定价,参与者付费"),
    SPONSORED("SPONSORED", "商家赞助", "商家赞助的活动");

    private final String code;
    private final String name;
    private final String description;

    PaymentType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static PaymentType fromCode(String code) {
        for (PaymentType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return FREE;
    }
}
