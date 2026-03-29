package com.im.backend.modules.bi.enums;

import lombok.Getter;

/**
 * 营销活动类型枚举
 */
@Getter
public enum CampaignType {

    COUPON("coupon", "优惠券", "满减券/折扣券/立减券"),
    FLASH_SALE("flash_sale", "限时秒杀", "限时特价抢购"),
    GROUP_BUY("group_buy", "拼团", "多人拼团优惠"),
    DISCOUNT("discount", "满减活动", "满额立减"),
    GIFT("gift", "买赠", "购买赠送"),
    MEMBER("member", "会员专享", "会员专属优惠"),
    FIRST_ORDER("first_order", "首单优惠", "新客首单立减"),
    REFERRAL("referral", "推荐有礼", "邀请好友奖励");

    private final String code;
    private final String name;
    private final String description;

    CampaignType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
