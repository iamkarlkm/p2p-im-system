package com.im.backend.modules.merchant.bi.enums;

/**
 * 营销类型枚举
 */
public enum MarketingType {

    /** 优惠券 */
    COUPON("coupon", "优惠券"),

    /** 满减活动 */
    FULL_REDUCTION("full_reduction", "满减活动"),

    /** 折扣活动 */
    DISCOUNT("discount", "折扣活动"),

    /** 秒杀活动 */
    SECKILL("seckill", "秒杀活动"),

    /** 拼团活动 */
    GROUP_BUY("group_buy", "拼团活动"),

    /** 新客专享 */
    NEW_USER("new_user", "新客专享"),

    /** 会员专享 */
    MEMBER("member", "会员专享"),

    /** 积分兑换 */
    POINTS("points", "积分兑换");

    private final String code;
    private final String desc;

    MarketingType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
