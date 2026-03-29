package com.im.backend.modules.local_life.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评价点赞实体类
 * 记录用户对评价的点赞
 */
@Data
@TableName("merchant_review_like")
public class MerchantReviewLike {

    /** 点赞ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 用户ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createTime;
}
