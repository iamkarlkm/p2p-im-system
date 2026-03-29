package com.im.local.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价图片/视频资源实体
 */
@Data
@TableName("merchant_review_media")
public class MerchantReviewMedia {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 媒体类型：1-图片 2-视频 */
    private Integer mediaType;

    /** 媒体URL */
    private String mediaUrl;

    /** 缩略图URL */
    private String thumbnailUrl;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 视频时长（秒） */
    private Integer duration;

    /** 宽度 */
    private Integer width;

    /** 高度 */
    private Integer height;

    /** 排序顺序 */
    private Integer sortOrder;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 是否删除 */
    @TableLogic
    private Integer deleted;
}
