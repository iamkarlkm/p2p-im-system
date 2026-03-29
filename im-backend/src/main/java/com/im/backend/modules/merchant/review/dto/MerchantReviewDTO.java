package com.im.backend.modules.merchant.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商户评价DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "商户评价DTO")
public class MerchantReviewDTO {

    @Schema(description = "评价ID")
    private Long reviewId;

    @Schema(description = "商户ID", required = true)
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    @Schema(description = "POI兴趣点ID")
    private Long poiId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "综合评分(1-5)", required = true)
    @NotNull(message = "综合评分不能为空")
    @DecimalMin(value = "1.0", message = "评分最低为1分")
    @DecimalMax(value = "5.0", message = "评分最高为5分")
    private BigDecimal overallRating;

    @Schema(description = "口味评分(1-5)")
    @DecimalMin(value = "1.0", message = "评分最低为1分")
    @DecimalMax(value = "5.0", message = "评分最高为5分")
    private BigDecimal tasteRating;

    @Schema(description = "环境评分(1-5)")
    @DecimalMin(value = "1.0", message = "评分最低为1分")
    @DecimalMax(value = "5.0", message = "评分最高为5分")
    private BigDecimal environmentRating;

    @Schema(description = "服务评分(1-5)")
    @DecimalMin(value = "1.0", message = "评分最低为1分")
    @DecimalMax(value = "5.0", message = "评分最高为5分")
    private BigDecimal serviceRating;

    @Schema(description = "性价比评分(1-5)")
    @DecimalMin(value = "1.0", message = "评分最低为1分")
    @DecimalMax(value = "5.0", message = "评分最高为5分")
    private BigDecimal valueRating;

    @Schema(description = "评价内容")
    @Size(max = 2000, message = "评价内容最多2000字")
    private String content;

    @Schema(description = "评价状态:0-待审核 1-已通过 2-已拒绝")
    private Integer status;

    @Schema(description = "是否匿名:0-实名 1-匿名")
    private Integer isAnonymous;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "回复数")
    private Integer replyCount;

    @Schema(description = "浏览数")
    private Integer viewCount;

    @Schema(description = "AI质量评分")
    private Integer aiQualityScore;

    @Schema(description = "是否为虚假评价")
    private Integer isFake;

    @Schema(description = "商家回复内容")
    private String merchantReply;

    @Schema(description = "商家回复时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime merchantReplyTime;

    @Schema(description = "消费金额")
    private BigDecimal consumptionAmount;

    @Schema(description = "消费时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime consumptionTime;

    @Schema(description = "是否推荐")
    private Integer isRecommend;

    @Schema(description = "标签列表")
    private List<String> tagList;

    @Schema(description = "评价媒体列表")
    private List<ReviewMediaDTO> mediaList;

    @Schema(description = "用户昵称")
    private String userNickname;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "是否已点赞（当前用户）")
    private Boolean hasLiked;

    @Schema(description = "平均维度评分")
    private BigDecimal averageDimensionRating;

    /**
     * 检查是否为优质评价
     */
    public boolean isHighQuality() {
        return aiQualityScore != null && aiQualityScore >= 80 &&
               overallRating != null && overallRating.compareTo(new BigDecimal("4")) >= 0;
    }

    /**
     * 计算综合评分（各维度平均值）
     */
    public BigDecimal calculateAverageRating() {
        int count = 0;
        BigDecimal sum = BigDecimal.ZERO;
        
        if (tasteRating != null) { sum = sum.add(tasteRating); count++; }
        if (environmentRating != null) { sum = sum.add(environmentRating); count++; }
        if (serviceRating != null) { sum = sum.add(serviceRating); count++; }
        if (valueRating != null) { sum = sum.add(valueRating); count++; }
        
        return count > 0 ? sum.divide(BigDecimal.valueOf(count), 1, BigDecimal.ROUND_HALF_UP) : overallRating;
    }
}
