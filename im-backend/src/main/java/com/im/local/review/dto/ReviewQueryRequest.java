package com.im.local.review.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 评价查询请求DTO
 */
@Data
public class ReviewQueryRequest {

    /** 商户ID */
    private Long merchantId;

    /** 用户ID */
    private Long userId;

    /** 评分筛选（最低分） */
    private Integer minRating;

    /** 评分筛选（最高分） */
    private Integer maxRating;

    /** 是否有图：0-全部 1-有图 */
    private Integer hasImage;

    /** 排序方式：latest/highest/lowest/liked/recommended */
    private String sortType;

    /** 页码 */
    private Integer pageNum = 1;

    /** 每页数量 */
    private Integer pageSize = 10;
}
