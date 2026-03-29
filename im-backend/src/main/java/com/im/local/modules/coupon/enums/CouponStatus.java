package com.im.local.modules.coupon.enums;

import lombok.Getter;

/**
 * 优惠券状态枚举
 * @author IM Development Team
 * @since 2026-03-28
 */
@Getter
public enum CouponStatus {

    NOT_STARTED(0, "未开始", "secondary"),
    ACTIVE(1, "进行中", "success"),
    ENDED(2, "已结束", "default"),
    STOPPED(3, "已停发", "warning");

    private final Integer code;
    private final String name;
    private final String tagType;

    CouponStatus(Integer code, String name, String tagType) {
        this.code = code;
        this.name = name;
        this.tagType = tagType;
    }

    public static CouponStatus getByCode(Integer code) {
        for (CouponStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        CouponStatus status = getByCode(code);
        return status != null ? status.getName() : "未知";
    }
}
