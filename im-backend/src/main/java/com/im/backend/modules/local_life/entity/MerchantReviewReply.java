package com.im.backend.modules.local_life.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评价回复实体类
 * 支持商家回复、用户追评、互相回复
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("merchant_review_reply")
public class MerchantReviewReply extends BaseEntity {

    /** 回复ID */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 父回复ID（用于楼中楼回复） */
    private Long parentId;

    /** 回复者类型：1-用户 2-商家 3-平台官方 */
    private Integer replyType;

    /** 回复者ID */
    private Long replyBy;

    /** 回复者名称 */
    private String replyName;

    /** 回复者头像 */
    private String replyAvatar;

    /** 被回复者ID */
    private Long replyTo;

    /** 被回复者名称 */
    private String replyToName;

    /** 回复内容 */
    private String content;

    /** 回复图片（JSON数组） */
    private String images;

    /** 点赞数 */
    private Integer likeCount;

    /** 回复状态：0-待审核 1-已通过 2-已拒绝 */
    private Integer status;

    /** 是否官方认证回复 */
    private Boolean official;

    /** 是否商家置顶回复 */
    private Boolean merchantPinned;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 逻辑删除 */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Boolean deleted;
}
