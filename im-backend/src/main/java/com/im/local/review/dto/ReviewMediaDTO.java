package com.im.local.review.dto;

import lombok.Data;

/**
 * 评价媒体DTO
 */
@Data
public class ReviewMediaDTO {

    /** 媒体类型：1-图片 2-视频 */
    private Integer mediaType;

    /** 媒体URL */
    private String mediaUrl;

    /** 缩略图URL */
    private String thumbnailUrl;

    /** 视频时长（秒） */
    private Integer duration;

    /** 宽度 */
    private Integer width;

    /** 高度 */
    private Integer height;
}
