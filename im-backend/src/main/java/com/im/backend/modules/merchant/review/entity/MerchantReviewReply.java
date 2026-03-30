package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 商户评价回复实体 - 功能#310: 本地商户评价口碑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_review_reply")
public class MerchantReviewReply {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 回复用户ID */
    private Long userId;

    /** 回复内容 */
    private String content;

    /** 回复类型: 1-商家回复, 2-用户追评, 3-其他用户回复 */
    private Integer replyType;

    /** 父回复ID (用于多级回复) */
    private Long parentId;

    /** 点赞数 */
    private Integer likeCount;

    /** 状态: 0-待审核, 1-已通过 */
    private Integer status;

    /** 是否删除 */
    @TableLogic
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
