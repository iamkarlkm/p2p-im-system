package com.im.backend.modules.miniprogram.market.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小程序详情响应DTO
 */
@Data
public class MiniProgramResponse {

    private Long id;
    private String appKey;
    private String appName;
    private String description;
    private String iconUrl;
    private List<String> screenshots;

    /**
     * 开发者信息
     */
    private DeveloperInfo developer;

    /**
     * 分类信息
     */
    private CategoryInfo category;

    private List<String> sceneTags;
    private String version;
    private Integer status;
    private String statusText;

    /**
     * 评分信息
     */
    private BigDecimal rating;
    private Integer ratingCount;

    /**
     * 使用统计
     */
    private Long downloadCount;
    private Long dau;

    /**
     * 是否已收藏
     */
    private Boolean isFavorited;

    /**
     * 是否推荐
     */
    private Boolean isRecommended;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Data
    public static class DeveloperInfo {
        private Long id;
        private String developerName;
        private Integer developerLevel;
        private Double rating;
    }

    @Data
    public static class CategoryInfo {
        private String categoryCode;
        private String categoryName;
        private String parentCode;
        private String parentName;
    }
}
