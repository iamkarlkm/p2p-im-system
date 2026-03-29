package com.im.backend.modules.merchant.review.enums;

/**
 * 评价排序类型枚举
 */
public enum ReviewSortType {
    DEFAULT("default", "默认排序"),
    NEWEST("newest", "最新发布"),
    HIGHEST_RATING("highest", "评分最高"),
    LOWEST_RATING("lowest", "评分最低"),
    MOST_HELPFUL("helpful", "最有帮助"),
    HAS_IMAGE("image", "有图评价"),
    HAS_VIDEO("video", "视频评价");

    private final String code;
    private final String desc;

    ReviewSortType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ReviewSortType fromCode(String code) {
        for (ReviewSortType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return DEFAULT;
    }
}
