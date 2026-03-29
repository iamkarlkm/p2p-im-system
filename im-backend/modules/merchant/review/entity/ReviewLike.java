package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价点赞实体类
 * 记录用户对评价的点赞行为
 * @author IM Development Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("review_like")
public class ReviewLike implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 点赞ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 用户ID */
    private Long userId;

    /** 商户ID */
    private Long merchantId;

    /** 点赞类型：1-评价点赞 2-回复点赞 */
    private Integer likeType;

    /** 目标ID（回复ID，如果是回复点赞） */
    private Long targetId;

    /** 点赞状态：0-取消 1-有效 */
    private Integer status;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否为有效点赞
     */
    public boolean isValid() {
        return status != null && status == STATUS_VALID;
    }

    /**
     * 点赞
     */
    public void like() {
        this.status = STATUS_VALID;
    }

    /**
     * 取消点赞
     */
    public void unlike() {
        this.status = STATUS_CANCELLED;
    }

    // ============ 静态常量 ============

    /** 点赞类型 */
    public static final int LIKE_TYPE_REVIEW = 1;      // 评价点赞
    public static final int LIKE_TYPE_REPLY = 2;       // 回复点赞

    /** 点赞状态 */
    public static final int STATUS_CANCELLED = 0;      // 取消
    public static final int STATUS_VALID = 1;          // 有效
}
