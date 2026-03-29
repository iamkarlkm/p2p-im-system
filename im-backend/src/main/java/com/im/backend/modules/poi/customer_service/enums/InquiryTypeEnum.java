package com.im.backend.modules.poi.customer_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 咨询类型枚举
 */
@Getter
@AllArgsConstructor
public enum InquiryTypeEnum {
    CONSULT("CONSULT", "商品咨询"),
    ORDER("ORDER", "订单问题"),
    AFTER_SALE("AFTER_SALE", "售后服务"),
    COMPLAINT("COMPLAINT", "投诉建议"),
    PRICE("PRICE", "价格咨询"),
    RESERVATION("RESERVATION", "预约咨询"),
    OTHER("OTHER", "其他咨询");

    private final String code;
    private final String desc;

    public static InquiryTypeEnum getByCode(String code) {
        for (InquiryTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return OTHER;
    }
}
