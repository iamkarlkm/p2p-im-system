package com.im.backend.modules.merchant.review.dto;

import lombok.Data;
import java.util.List;

/**
 * 评价列表响应DTO
 */
@Data
public class ReviewListResponse {

    private List<ReviewDetailResponse> reviews;
    private Long total;
    private Integer page;
    private Integer size;
    private Boolean hasMore;
}
