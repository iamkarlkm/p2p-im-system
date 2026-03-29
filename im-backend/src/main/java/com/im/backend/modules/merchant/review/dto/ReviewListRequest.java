package com.im.backend.modules.merchant.review.dto;

import lombok.Data;

import java.util.List;

/**
 * 评价列表查询请求DTO
 */
@Data
public class ReviewListRequest {

    private Long merchantId;
    private String sortType = "default"; // default/newest/highest/lowest/helpful/image/video
    private Integer rating; // 筛选特定评分 1-5
    private Boolean hasImage;
    private Boolean hasVideo;
    private Integer page = 1;
    private Integer size = 20;
}
