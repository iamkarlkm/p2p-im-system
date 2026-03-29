package com.im.local.modules.coupon.enums;

import lombok.Getter;

/**
 * 用户优惠券状态枚举
 * @author IM Development Team
 * @since 2026-03-28
 */
@Getter
public enum UserCouponStatus {

    UNUSED(0, "未使用", "success"),
    USED(1, "已使用", "default"),
    EXPIRED(2, "已过期", "info"),
    INVALID(3, "已作废", "danger");

    private final Integer code;
    private final String name;
    private final String tagType;

    UserCouponStatus(Integer code, String name, String tagType) {
        this.code = code;
        this.name = name;
        this.tagType = tagType;
    }

    public static UserCouponStatus getByCode(Integer code) {
        for (UserCouponStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        UserCouponStatus status = getByCode(code);
        return status != null ? status.getName() : "未知";
    }

    /**
     * 是否可用状态
     */
    public boolean isUsable() {
        return this == UNUSED;
    }
}
