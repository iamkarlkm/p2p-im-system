package com.im.backend.modules.merchant.order.enums;

import lombok.Getter;

/**
 * 配送异常类型枚举
 */
@Getter
public enum DeliveryExceptionType {

    CANNOT_CONTACT_CUSTOMER(1, "联系不上顾客"),
    WRONG_ADDRESS(2, "地址错误"),
    CUSTOMER_REJECTED(3, "顾客拒收"),
    VEHICLE_BREAKDOWN(4, "车辆故障"),
    TRAFFIC_ACCIDENT(5, "交通事故"),
    WEATHER_ISSUE(6, "天气原因"),
    MERCHANT_DELAY(7, "商家出餐慢"),
    ROAD_CLOSED(8, "道路封闭"),
    OTHER(9, "其他");

    private final int code;
    private final String desc;

    DeliveryExceptionType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DeliveryExceptionType fromCode(int code) {
        for (DeliveryExceptionType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
