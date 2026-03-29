package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评价媒体资源实体类
 * 支持图片和视频评价
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_media")
public class ReviewMedia extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 媒体ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long mediaId;

    /**
     * 评价ID
     */
    private Long reviewId;

    /**
     * 媒体类型：1-图片 2-视频
     */
    private Integer mediaType;

    /**
     * 原始文件URL
     */
    private String originalUrl;

    /**
     * 压缩/缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件格式（jpg/png/mp4等）
     */
    private String fileFormat;

    /**
     * 图片宽度（像素）
     */
    private Integer width;

    /**
     * 图片高度（像素）
     */
    private Integer height;

    /**
     * 视频时长（秒）
     */
    private Integer videoDuration;

    /**
     * 视频封面URL
     */
    private String videoCoverUrl;

    /**
     * 排序序号
     */
    private Integer sortOrder;

    /**
     * 媒体状态：0-上传中 1-处理中 2-已完成 3-失败
     */
    private Integer status;

    /**
     * CDN加速URL
     */
    private String cdnUrl;

    /**
     * 文件MD5校验值
     */
    private String fileMd5;

    /**
     * 是否AI审核通过：0-待审核 1-通过 2-拒绝
     */
    private Integer aiAuditStatus;

    /**
     * AI审核结果说明
     */
    private String aiAuditResult;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Integer deleted;

    /**
     * 检查是否为视频
     */
    public boolean isVideo() {
        return mediaType != null && mediaType == 2;
    }

    /**
     * 检查是否为图片
     */
    public boolean isImage() {
        return mediaType != null && mediaType == 1;
    }

    /**
     * 获取展示URL
     */
    public String getDisplayUrl() {
        if (cdnUrl != null && !cdnUrl.isEmpty()) {
            return cdnUrl;
        }
        return thumbnailUrl != null ? thumbnailUrl : originalUrl;
    }

    /**
     * 获取文件大小（MB）
     */
    public Double getFileSizeInMB() {
        if (fileSize == null) return 0.0;
        return fileSize / (1024.0 * 1024.0);
    }
}
