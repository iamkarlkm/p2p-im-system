package com.im.backend.modules.poi.customer_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 客服类型枚举
 */
@Getter
@AllArgsConstructor
public enum CustomerServiceTypeEnum {
    HUMAN("HUMAN", "人工客服"),
    ROBOT("ROBOT", "机器人客服"),
    HYBRID("HYBRID", "混合模式");

    private final String code;
    private final String desc;

    public static CustomerServiceTypeEnum getByCode(String code) {
        for (CustomerServiceTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return HUMAN;
    }
}
