package com.im.backend.modules.merchant.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建评价请求DTO
 * @author IM Development Team
 * @version 1.0.0
 */
@Data
@Schema(description = "创建评价请求")
public class CreateReviewRequest {

    @Schema(description = "商户ID", required = true)
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    @Schema(description = "POI兴趣点ID")
    private Long poiId;

    @Schema(description = "订单ID（消费验证）")
    private Long orderId;

    @Schema(description = "总体星级评分(1-5)", required = true)
    @NotNull(message = "评分不能为空")
    @DecimalMin(value = "1.0", message = "评分最低1分")
    @DecimalMax(value = "5.0", message = "评分最高5分")
    private BigDecimal overallRating;

    @Schema(description = "评价标题")
    @Size(max = 50, message = "标题最多50字")
    private String title;

    @Schema(description = "评价内容", required = true)
    @NotBlank(message = "评价内容不能为空")
    @Size(min = 5, max = 2000, message = "评价内容5-2000字")
    private String content;

    @Schema(description = "评价类型：1-文字 2-图文 3-视频")
    private Integer reviewType = 1;

    @Schema(description = "消费金额")
    @DecimalMin(value = "0", message = "金额不能为负")
    private BigDecimal consumptionAmount;

    @Schema(description = "消费时间")
    private LocalDateTime consumptionTime;

    @Schema(description = "人均消费")
    private BigDecimal perCapitaAmount;

    @Schema(description = "是否推荐：0-不推荐 1-推荐")
    private Integer isRecommended = 1;

    @Schema(description = "是否匿名：0-实名 1-匿名")
    private Integer isAnonymous = 0;

    @Schema(description = "评价维度列表")
    private List<DimensionDTO> dimensionList;

    @Schema(description = "媒体列表（图片/视频URL）")
    private List<MediaDTO> mediaList;

    /**
     * 评价维度DTO
     */
    @Data
    @Schema(description = "评价维度")
    public static class DimensionDTO {

        @Schema(description = "维度编码", required = true)
        @NotBlank(message = "维度编码不能为空")
        private String dimensionCode;

        @Schema(description = "维度名称")
        private String dimensionName;

        @Schema(description = "维度评分(1-5)", required = true)
        @NotNull(message = "维度评分不能为空")
        @DecimalMin(value = "1.0", message = "评分最低1分")
        @DecimalMax(value = "5.0", message = "评分最高5分")
        private BigDecimal rating;

        @Schema(description = "维度描述/标签")
        private String tags;
    }

    /**
     * 媒体DTO
     */
    @Data
    @Schema(description = "评价媒体")
    public static class MediaDTO {

        @Schema(description = "媒体类型：1-图片 2-视频", required = true)
        @NotNull(message = "媒体类型不能为空")
        private Integer mediaType;

        @Schema(description = "媒体URL", required = true)
        @NotBlank(message = "媒体URL不能为空")
        private String mediaUrl;

        @Schema(description = "缩略图URL")
        private String thumbnailUrl;

        @Schema(description = "媒体描述")
        private String description;

        @Schema(description = "视频时长（秒）")
        private Integer duration;

        @Schema(description = "视频封面图")
        private String videoCover;

        @Schema(description = "宽度（像素）")
        private Integer width;

        @Schema(description = "高度（像素）")
        private Integer height;
    }
}
