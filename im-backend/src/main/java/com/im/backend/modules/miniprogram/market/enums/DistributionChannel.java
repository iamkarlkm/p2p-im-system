package com.im.backend.modules.miniprogram.market.enums;

import lombok.Getter;

/**
 * 分发渠道枚举
 */
@Getter
public enum DistributionChannel {

    RECOMMEND(1, "智能推荐"),
    SEARCH(2, "搜索发现"),
    CATEGORY(3, "分类浏览"),
    SHARE(4, "社交分享"),
    AD(5, "广告投放");

    private final Integer code;
    private final String description;

    DistributionChannel(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DistributionChannel fromCode(Integer code) {
        for (DistributionChannel channel : values()) {
            if (channel.code.equals(code)) {
                return channel;
            }
        }
        return RECOMMEND;
    }
}
