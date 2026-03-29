package com.im.backend.modules.merchant.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 评价媒体DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "评价媒体DTO")
public class ReviewMediaDTO {

    @Schema(description = "媒体ID")
    private Long mediaId;

    @Schema(description = "评价ID")
    private Long reviewId;

    @Schema(description = "媒体类型:1-图片 2-视频")
    private Integer mediaType;

    @Schema(description = "原始文件URL")
    private String originalUrl;

    @Schema(description = "缩略图URL")
    private String thumbnailUrl;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "文件格式")
    private String fileFormat;

    @Schema(description = "图片宽度")
    private Integer width;

    @Schema(description = "图片高度")
    private Integer height;

    @Schema(description = "视频时长(秒)")
    private Integer videoDuration;

    @Schema(description = "视频封面URL")
    private String videoCoverUrl;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "CDN加速URL")
    private String cdnUrl;

    @Schema(description = "展示URL")
    private String displayUrl;

    @Schema(description = "是否为视频")
    private Boolean isVideo;
}
