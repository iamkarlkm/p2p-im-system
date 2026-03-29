package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 点赞/取消点赞请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class LikeReviewRequestDTO {
    
    /** 评价ID */
    @NotNull(message = "评价ID不能为空")
    private Long reviewId;
    
    /** 是否点赞 (true=点赞, false=取消) */
    @NotNull
    private Boolean like;
}
