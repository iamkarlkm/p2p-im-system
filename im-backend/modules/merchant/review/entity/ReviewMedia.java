package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价媒体实体类
 * 支持图片、短视频等多媒体评价内容
 * @author IM Development Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("review_media")
public class ReviewMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 媒体ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 商户ID */
    private Long merchantId;

    /** 用户ID */
    private Long userId;

    /** 媒体类型：1-图片 2-视频 */
    private Integer mediaType;

    /** 媒体URL */
    private String mediaUrl;

    /** 缩略图URL */
    private String thumbnailUrl;

    /** 原始文件URL */
    private String originalUrl;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件格式 */
    private String fileFormat;

    /** 宽度（像素） */
    private Integer width;

    /** 高度（像素） */
    private Integer height;

    /** 视频时长（秒） */
    private Integer duration;

    /** 视频封面图 */
    private String videoCover;

    /** 媒体描述 */
    private String description;

    /** 排序序号 */
    private Integer sortOrder;

    /** AI审核状态：0-待审核 1-通过 2-拒绝 */
    private Integer aiAuditStatus;

    /** AI审核结果 */
    private String aiAuditResult;

    /** 人工审核状态：0-待审核 1-通过 2-拒绝 */
    private Integer manualAuditStatus;

    /** 审核时间 */
    private LocalDateTime auditTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 删除标记 */
    @TableLogic
    private Integer deleted;

    /**
     * 是否为视频
     */
    public boolean isVideo() {
        return mediaType != null && mediaType == MEDIA_TYPE_VIDEO;
    }

    /**
     * 是否为图片
     */
    public boolean isImage() {
        return mediaType == null || mediaType == MEDIA_TYPE_IMAGE;
    }

    /**
     * 获取显示URL
     */
    public String getDisplayUrl() {
        if (isVideo() && thumbnailUrl != null) {
            return thumbnailUrl;
        }
        return mediaUrl;
    }

    /**
     * 审核通过
     */
    public void approve() {
        this.aiAuditStatus = AUDIT_STATUS_APPROVED;
        this.manualAuditStatus = AUDIT_STATUS_APPROVED;
        this.auditTime = LocalDateTime.now();
    }

    /**
     * 审核拒绝
     */
    public void reject(String reason) {
        this.aiAuditStatus = AUDIT_STATUS_REJECTED;
        this.manualAuditStatus = AUDIT_STATUS_REJECTED;
        this.aiAuditResult = reason;
        this.auditTime = LocalDateTime.now();
    }

    // ============ 静态常量 ============

    /** 媒体类型 */
    public static final int MEDIA_TYPE_IMAGE = 1;      // 图片
    public static final int MEDIA_TYPE_VIDEO = 2;      // 视频

    /** 审核状态 */
    public static final int AUDIT_STATUS_PENDING = 0;  // 待审核
    public static final int AUDIT_STATUS_APPROVED = 1; // 通过
    public static final int AUDIT_STATUS_REJECTED = 2; // 拒绝
}
