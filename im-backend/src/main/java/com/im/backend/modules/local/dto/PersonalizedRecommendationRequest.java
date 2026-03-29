package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 个性化推荐请求DTO
 * 支持多路召回推荐引擎
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "个性化推荐请求")
public class PersonalizedRecommendationRequest {
    
    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "user_123456", required = true)
    private String userId;
    
    @Schema(description = "用户当前位置纬度", example = "31.230416")
    private Double latitude;
    
    @Schema(description = "用户当前位置经度", example = "121.473701")
    private Double longitude;
    
    @Schema(description = "推荐场景：feed/nearby/popular/scene", example = "feed")
    private String scene;
    
    @Schema(description = "分页页码", example = "1")
    private Integer page;
    
    @Schema(description = "每页数量", example = "20")
    private Integer pageSize;
    
    @Schema(description = "召回策略列表：geo/hot/cf/vector/", example = "[\"geo\", \"hot\", \"cf\"]")
    private List<String> recallStrategies;
    
    @Schema(description = "排序策略：relevance/distance/rating/popularity", example = "relevance")
    private String sortStrategy;
    
    @Schema(description = "内容类型过滤：poi/activity/coupon", example = "[\"poi\"]")
    private List<String> contentTypes;
    
    @Schema(description = "是否去重", example = "true")
    private Boolean deduplicate;
    
    @Schema(description = "已曝光内容ID列表（用于去重）")
    private List<String> exposedItems;
    
    @Schema(description = "时间上下文：breakfast/lunch/dinner/night", example = "dinner")
    private String timeContext;
    
    @Schema(description = "天气上下文：sunny/rainy/cloudy", example = "sunny")
    private String weatherContext;
    
    @Schema(description = "社交上下文：alone/couple/family/friends", example = "couple")
    private String socialContext;
    
    @Schema(description = "扩展参数")
    private Map<String, Object> extraParams;
}
