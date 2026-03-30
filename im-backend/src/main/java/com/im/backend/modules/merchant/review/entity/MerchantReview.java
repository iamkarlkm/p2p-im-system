package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户评价实体 - 功能#310: 本地商户评价口碑
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_review")
public class MerchantReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 用户ID */
    private Long userId;

    /** 订单ID */
    private Long orderId;

    /** 评分 (1-5星) */
    private Integer rating;

    /** 评价内容 */
    private String content;

    /** 评价标签 (逗号分隔) */
    private String tags;

    /** 评价图片URL列表 (JSON数组) */
    private String images;

    /** 评价视频URL */
    private String videoUrl;

    /** 是否匿名 */
    private Boolean anonymous;

    /** 点赞数 */
    private Integer likeCount;

    /** 回复数 */
    private Integer replyCount;

    /** 浏览数 */
    private Integer viewCount;

    /** 商家回复内容 */
    private String merchantReply;

    /** 商家回复时间 */
    private LocalDateTime merchantReplyTime;

    /** 是否推荐 (优质评价) */
    private Boolean recommended;

    /** 评价状态: 0-待审核, 1-已通过, 2-已拒绝 */
    private Integer status;

    /** 拒绝原因 */
    private String rejectReason;

    /** 消费金额 */
    private java.math.BigDecimal consumeAmount;

    /** 消费项目 */
    private String consumeItems;

    /** 是否删除 */
    @TableLogic
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
