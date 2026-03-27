package com.im.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 推荐统计数据
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "推荐统计数据")
public class RecommendationStats {

    @Schema(description = "总推荐数量", example = "50")
    private Long totalRecommendations;

    @Schema(description = "基于共同好友的推荐数量", example = "20")
    private Long mutualFriendRecommendations;

    @Schema(description = "基于兴趣标签的推荐数量", example = "15")
    private Long interestTagRecommendations;

    @Schema(description = "基于群组关系的推荐数量", example = "15")
    private Long groupRelationRecommendations;

    @Schema(description = "已忽略的推荐数量", example = "10")
    private Long ignoredRecommendations;

    @Schema(description = "已添加的好友数量（来自推荐）", example = "5")
    private Long addedFromRecommendations;

    @Schema(description = "推荐准确率（0-1）", example = "0.75")
    private Double accuracyRate;

    @Schema(description = "最后一次刷新时间")
    private String lastRefreshTime;
}
