package com.im.backend.modules.delivery.enums;

import lombok.Getter;

/**
 * 配送订单类型枚举
 */
@Getter
public enum DeliveryType {
    
    INSTANT(1, "即时配送"),
    SCHEDULED(2, "预约配送"),
    EXPRESS(3, "快递配送"),
    SAME_CITY(4, "同城配送"),
    CROSS_CITY(5, "跨城配送");
    
    private final Integer code;
    private final String desc;
    
    DeliveryType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
