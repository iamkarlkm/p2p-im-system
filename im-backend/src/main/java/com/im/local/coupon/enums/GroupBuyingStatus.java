package com.im.local.coupon.enums;

/**
 * 拼团状态枚举
 */
public enum GroupBuyingStatus {

    /** 拼团中 */
    IN_PROGRESS(0, "拼团中", "grouping"),

    /** 成团成功 */
    SUCCESS(1, "成团成功", "success"),

    /** 成团失败 */
    FAILED(2, "成团失败", "failed");

    private final Integer code;
    private final String name;
    private final String tag;

    GroupBuyingStatus(Integer code, String name, String tag) {
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

    public static GroupBuyingStatus getByCode(Integer code) {
        for (GroupBuyingStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
