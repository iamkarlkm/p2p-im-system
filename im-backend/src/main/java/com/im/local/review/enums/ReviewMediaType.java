package com.im.local.review.enums;

import lombok.Getter;

/**
 * 媒体类型枚举
 */
@Getter
public enum ReviewMediaType {

    IMAGE(1, "图片"),
    VIDEO(2, "视频");

    private final Integer code;
    private final String desc;

    ReviewMediaType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
