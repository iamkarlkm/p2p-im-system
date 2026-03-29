package com.im.local.modules.coupon.enums;

import lombok.Getter;

/**
 * 营销活动类型枚举
 * @author IM Development Team
 * @since 2026-03-28
 */
@Getter
public enum CampaignType {

    FULL_REDUCTION(1, "满减活动", "满额立减优惠"),
    DISCOUNT(2, "折扣活动", "全场折扣优惠"),
    SECKILL(3, "秒杀活动", "限时限量秒杀"),
    GROUP_BUY(4, "拼团活动", "多人拼团优惠"),
    BARGAIN(5, "砍价活动", "邀请好友砍价"),
    LOTTERY(6, "抽奖活动", "幸运大抽奖");

    private final Integer code;
    private final String name;
    private final String description;

    CampaignType(Integer code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static CampaignType getByCode(Integer code) {
        for (CampaignType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        CampaignType type = getByCode(code);
        return type != null ? type.getName() : "未知";
    }
}
