package com.im.local.coupon.enums;

/**
 * 用户优惠券状态枚举
 */
public enum UserCouponStatus {

    /** 未使用 */
    UNUSED(0, "未使用", "valid"),

    /** 已使用 */
    USED(1, "已使用", "used"),

    /** 已过期 */
    EXPIRED(2, "已过期", "expired"),

    /** 已作废 */
    INVALID(3, "已作废", "invalid");

    private final Integer code;
    private final String name;
    private final String tag;

    UserCouponStatus(Integer code, String name, String tag) {
        this.code = code;
        this.name = name;
        this.tag = tag;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public static UserCouponStatus getByCode(Integer code) {
        for (UserCouponStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
