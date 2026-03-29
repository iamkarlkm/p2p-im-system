package com.im.local.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价点赞记录实体
 */
@Data
@TableName("merchant_review_like")
public class MerchantReviewLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 用户ID */
    private Long userId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 唯一索引防止重复点赞 */
    @TableField(exist = false)
    private String uniqueKey;
}
