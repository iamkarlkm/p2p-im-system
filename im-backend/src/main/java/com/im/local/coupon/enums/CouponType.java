package com.im.local.coupon.enums;

/**
 * 优惠券类型枚举
 */
public enum CouponType {

    /** 满减券 */
    FULL_REDUCTION(1, "满减券", "满足金额条件减固定金额"),

    /** 折扣券 */
    DISCOUNT(2, "折扣券", "按比例折扣"),

    /** 代金券 */
    CASH(3, "代金券", "直接抵扣固定金额"),

    /** 兑换券 */
    EXCHANGE(4, "兑换券", "兑换指定商品/服务");

    private final Integer code;
    private final String name;
    private final String desc;

    CouponType(Integer code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public static CouponType getByCode(Integer code) {
        for (CouponType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
