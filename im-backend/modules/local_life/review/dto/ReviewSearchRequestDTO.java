package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import java.util.List;

/**
 * 评价搜索请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class ReviewSearchRequestDTO {
    
    /** 商户ID */
    private Long merchantId;
    
    /** 用户ID */
    private Long userId;
    
    /** 评分筛选 (1-5) */
    private Integer rating;
    
    /** 最低评分 */
    private Integer minRating;
    
    /** 评价类型: ALL/WITH_IMAGE/WITH_VIDEO/HIGH_QUALITY */
    private String reviewType;
    
    /** 排序方式: NEWEST/MOST_LIKED/HIGHEST_RATING/LOWEST_RATING */
    private String sortBy;
    
    /** 页码 */
    private Integer pageNum = 1;
    
    /** 每页大小 */
    private Integer pageSize = 10;
    
    /** 标签筛选 */
    private List<String> tags;
    
    /** 关键词搜索 */
    private String keyword;
}
