package com.im.backend.modules.miniprogram.market.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小程序评分评论实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mini_program_review")
public class MiniProgramReview {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 小程序ID
     */
    private Long appId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评分（1-5分）
     */
    private BigDecimal rating;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论图片（JSON数组）
     */
    private String images;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 是否置顶
     */
    private Boolean isTop;

    /**
     * 状态：0-待审核 1-已展示 2-已隐藏
     */
    private Integer status;

    /**
     * 开发者回复
     */
    private String developerReply;

    /**
     * 回复时间
     */
    private LocalDateTime replyTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Boolean deleted;
}
