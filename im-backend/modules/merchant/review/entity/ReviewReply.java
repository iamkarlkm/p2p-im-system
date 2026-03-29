package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价回复实体类
 * 支持商家回复、用户追评、互相回复
 * @author IM Development Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("review_reply")
public class ReviewReply implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 回复ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 商户ID */
    private Long merchantId;

    /** 父回复ID（用于回复的回复） */
    private Long parentId;

    /** 回复者ID */
    private Long replyUserId;

    /** 回复者类型：1-用户 2-商家 3-平台 */
    private Integer replyUserType;

    /** 被回复者ID */
    private Long toUserId;

    /** 被回复者类型：1-用户 2-商家 3-平台 */
    private Integer toUserType;

    /** 回复内容 */
    private String content;

    /** 回复状态：0-待审核 1-已通过 2-已拒绝 */
    private Integer status;

    /** 点赞数 */
    private Integer likeCount;

    /** 是否商家官方回复 */
    private Integer isOfficial;

    /** 是否隐藏：0-显示 1-隐藏 */
    private Integer isHidden;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 删除标记 */
    @TableLogic
    private Integer deleted;

    /** 非持久化字段：回复者昵称 */
    @TableField(exist = false)
    private String replyUserNickname;

    @TableField(exist = false)
    private String replyUserAvatar;

    /** 非持久化字段：被回复者昵称 */
    @TableField(exist = false)
    private String toUserNickname;

    /**
     * 是否为商家回复
     */
    public boolean isMerchantReply() {
        return replyUserType != null && replyUserType == REPLY_TYPE_MERCHANT;
    }

    /**
     * 是否为用户回复
     */
    public boolean isUserReply() {
        return replyUserType == null || replyUserType == REPLY_TYPE_USER;
    }

    /**
     * 是否为一级回复（直接回复评价）
     */
    public boolean isTopLevelReply() {
        return parentId == null || parentId == 0;
    }

    /**
     * 增加点赞数
     */
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount != null ? this.likeCount : 0) + 1;
    }

    /**
     * 隐藏回复
     */
    public void hide() {
        this.isHidden = 1;
    }

    /**
     * 显示回复
     */
    public void show() {
        this.isHidden = 0;
    }

    /**
     * 审核通过
     */
    public void approve() {
        this.status = STATUS_APPROVED;
    }

    /**
     * 审核拒绝
     */
    public void reject() {
        this.status = STATUS_REJECTED;
    }

    // ============ 静态常量 ============

    /** 回复者类型 */
    public static final int REPLY_TYPE_USER = 1;       // 用户
    public static final int REPLY_TYPE_MERCHANT = 2;   // 商家
    public static final int REPLY_TYPE_PLATFORM = 3;   // 平台

    /** 回复状态 */
    public static final int STATUS_PENDING = 0;        // 待审核
    public static final int STATUS_APPROVED = 1;       // 已通过
    public static final int STATUS_REJECTED = 2;       // 已拒绝

    /** 官方回复标记 */
    public static final int NOT_OFFICIAL = 0;          // 非官方
    public static final int IS_OFFICIAL = 1;           // 官方回复

    /** 隐藏状态 */
    public static final int NOT_HIDDEN = 0;            // 显示
    public static final int IS_HIDDEN = 1;             // 隐藏
}
