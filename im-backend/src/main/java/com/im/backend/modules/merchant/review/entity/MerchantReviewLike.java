package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评价点赞记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_review_like")
public class MerchantReviewLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 点赞ID */
    private String likeId;

    /** 评价ID */
    private String reviewId;

    /** 用户ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
