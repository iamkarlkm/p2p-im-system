package com.im.local.coupon.enums;

/**
 * 营销活动类型枚举
 */
public enum ActivityType {

    /** 满减活动 */
    FULL_REDUCTION(1, "满减活动"),

    /** 折扣活动 */
    DISCOUNT(2, "折扣活动"),

    /** 秒杀活动 */
    FLASH_SALE(3, "秒杀活动"),

    /** 拼团活动 */
    GROUP_BUYING(4, "拼团活动"),

    /** 砍价活动 */
    BARGAIN(5, "砍价活动");

    private final Integer code;
    private final String name;

    ActivityType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ActivityType getByCode(Integer code) {
        for (ActivityType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
