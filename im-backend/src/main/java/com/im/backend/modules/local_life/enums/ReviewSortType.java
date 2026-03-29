package com.im.backend.modules.local_life.enums;

import lombok.Getter;

/**
 * 评价排序方式枚举
 */
@Getter
public enum ReviewSortType {

    LATEST("latest", "最新评价"),
    HIGHEST_RATING("highest", "评分最高"),
    LOWEST_RATING("lowest", "评分最低"),
    MOST_LIKED("liked", "最多点赞"),
    RECOMMENDED("recommended", "优质推荐"),
    WITH_IMAGE("image", "有图评价"),
    WITH_VIDEO("video", "视频评价");

    private final String code;
    private final String desc;

    ReviewSortType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReviewSortType fromCode(String code) {
        if (code == null) return LATEST;
        for (ReviewSortType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return LATEST;
    }
}
