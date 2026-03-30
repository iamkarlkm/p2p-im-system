package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商户评价点赞实体 - 功能#310: 本地商户评价口碑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_review_like")
public class MerchantReviewLike {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 用户ID */
    private Long userId;

    /** 点赞类型: 1-评价点赞, 2-回复点赞 */
    private Integer likeType;

    /** 关联ID (当likeType=2时,为replyId) */
    private Long refId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
