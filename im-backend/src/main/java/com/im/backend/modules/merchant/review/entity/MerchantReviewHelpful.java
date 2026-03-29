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
 * 评价 helpfulness 投票记录
 * 用户标记评价是否有帮助
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_review_helpful")
public class MerchantReviewHelpful {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评价ID */
    private String reviewId;

    /** 用户ID */
    private Long userId;

    /** 是否有帮助 1-有帮助 0-无帮助 */
    private Integer helpful;

    /** 创建时间 */
    private LocalDateTime createdAt;
}
