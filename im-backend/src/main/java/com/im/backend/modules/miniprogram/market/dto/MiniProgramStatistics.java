package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

/**
 * 小程序使用统计DTO
 */
@Data
public class MiniProgramStatistics {

    private Long appId;
    private String appName;

    /**
     * 总下载量
     */
    private Long totalDownloads;

    /**
     * 今日下载量
     */
    private Long todayDownloads;

    /**
     * 日活跃用户
     */
    private Long dau;

    /**
     * 月活跃用户
     */
    private Long mau;

    /**
     * 平均评分
     */
    private Double avgRating;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 收藏人数
     */
    private Long favoriteCount;

    /**
     * 分享次数
     */
    private Long shareCount;
}
