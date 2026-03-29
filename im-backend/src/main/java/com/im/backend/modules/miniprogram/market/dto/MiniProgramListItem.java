package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小程序列表项DTO
 */
@Data
public class MiniProgramListItem {

    private Long id;
    private String appKey;
    private String appName;
    private String description;
    private String iconUrl;
    private String categoryName;

    /**
     * 评分
     */
    private Double rating;

    /**
     * 下载量
     */
    private Long downloadCount;

    /**
     * 距离（米）
     */
    private Integer distance;

    /**
     * 是否推荐
     */
    private Boolean isRecommended;

    /**
     * 推荐理由
     */
    private String recommendReason;

    /**
     * 是否已收藏
     */
    private Boolean isFavorited;

    private LocalDateTime createTime;
}
