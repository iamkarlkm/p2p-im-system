package com.im.local.coupon.enums;

/**
 * 砍价状态枚举
 */
public enum BargainStatus {

    /** 砍价中 */
    IN_PROGRESS(0, "砍价中", "bargaining"),

    /** 砍价成功 */
    SUCCESS(1, "砍价成功", "success"),

    /** 砍价失败 */
    FAILED(2, "砍价失败", "failed"),

    /** 已购买 */
    PURCHASED(3, "已购买", "purchased");

    private final Integer code;
    private final String name;
    private final String tag;

    BargainStatus(Integer code, String name, String tag) {
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

    public static BargainStatus getByCode(Integer code) {
        for (BargainStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
