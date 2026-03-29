package com.im.local.modules.coupon.enums;

import lombok.Getter;

/**
 * 领取渠道枚举
 * @author IM Development Team
 * @since 2026-03-28
 */
@Getter
public enum ReceiveChannel {

    ACTIVE(1, "主动领取"),
    SYSTEM(2, "系统发放"),
    ACTIVITY(3, "活动赠送"),
    SHARE(4, "分享获得"),
    EXCHANGE(5, "积分兑换"),
    INVITE(6, "邀请奖励");

    private final Integer code;
    private final String name;

    ReceiveChannel(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ReceiveChannel getByCode(Integer code) {
        for (ReceiveChannel channel : values()) {
            if (channel.getCode().equals(code)) {
                return channel;
            }
        }
        return null;
    }

    public static String getNameByCode(Integer code) {
        ReceiveChannel channel = getByCode(code);
        return channel != null ? channel.getName() : "未知";
    }
}
