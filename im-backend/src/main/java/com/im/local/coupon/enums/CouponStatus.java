package com.im.local.coupon.enums;

/**
 * 优惠券状态枚举
 */
public enum CouponStatus {

    /** 草稿 */
    DRAFT(0, "草稿"),

    /** 未开始 */
    NOT_STARTED(1, "未开始"),

    /** 进行中 */
    IN_PROGRESS(2, "进行中"),

    /** 已结束 */
    ENDED(3, "已结束"),

    /** 已作废 */
    CANCELLED(4, "已作废");

    private final Integer code;
    private final String name;

    CouponStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static CouponStatus getByCode(Integer code) {
        for (CouponStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
