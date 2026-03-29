package com.im.backend.modules.local_life.review.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 回复评价请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class ReplyReviewRequestDTO {
    
    /** 评价ID */
    @NotNull(message = "评价ID不能为空")
    private Long reviewId;
    
    /** 父回复ID (回复某个回复时使用) */
    private Long parentReplyId;
    
    /** 回复内容 */
    @NotBlank(message = "回复内容不能为空")
    @Size(max = 500, message = "回复内容最多500字")
    private String content;
}
