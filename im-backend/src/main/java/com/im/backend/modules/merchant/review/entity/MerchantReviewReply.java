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
 * 评价回复实体类
 * 支持商家回复、用户追评、互动回复
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_review_reply")
public class MerchantReviewReply {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 回复ID */
    private String replyId;

    /** 评价ID */
    private String reviewId;

    /** 父回复ID（支持二级回复） */
    private String parentReplyId;

    /** 回复者ID */
    private Long replierId;

    /** 回复者类型 1-商户 2-用户 */
    private Integer replierType;

    /** 回复内容 */
    private String content;

    /** 回复图片 */
    private String images;

    /** 点赞数 */
    private Integer likeCount;

    /** 情感分析得分 */
    private Double sentimentScore;

    /** 状态 0-待审核 1-已通过 2-已拒绝 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
