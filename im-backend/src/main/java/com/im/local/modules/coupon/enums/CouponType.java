package com.im.local.modules.coupon.enums;

import lombok.Getter;

/**
 * 优惠券类型枚举
 * @author IM Development Team
 * @since 2026-03-28
 */
@Getter
public enum CouponType {

    FULL_REDUCTION(1, "满减券", "满%s减%s"),
    DISCOUNT(2, "折扣券", "%s折"),
    NO_THRESHOLD(3, "无门槛券", "%s元"),
    EXCHANGE(4, "兑换券", "兑换%s");

    private final Integer code;
    private final String name;
    private final String format;

    CouponType(Integer code, String name, String format) {
        this.code = code;
        this.name = name;
        this.format = format;
    }

    public static CouponType getByCode(Integer code) {
        for (CouponType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        CouponType type = getByCode(code);
        return type != null ? type.getName() : "未知";
    }

    /**
     * 格式化显示
     */
    public String formatDisplay(Object... args) {
        return String.format(format, args);
    }
}
