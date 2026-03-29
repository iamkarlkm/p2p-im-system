package com.im.local.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价回复实体
 */
@Data
@TableName("merchant_review_reply")
public class MerchantReviewReply {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 回复者类型：1-商户 2-用户 */
    private Integer replierType;

    /** 回复者ID */
    private Long replierId;

    /** 回复内容 */
    private String content;

    /** 父回复ID（支持二级回复） */
    private Long parentId;

    /** 点赞数 */
    private Integer likeCount;

    /** 状态：0-待审核 1-已发布 2-已删除 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /** 是否删除 */
    @TableLogic
    private Integer deleted;
}
