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
 * POI语义搜索请求DTO
 * 支持基于自然语言的POI搜索
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "POI语义搜索请求")
public class POISemanticSearchRequest {
    
    @NotBlank(message = "搜索语义不能为空")
    @Schema(description = "自然语言搜索语义", example = "附近适合约会的西餐厅", required = true)
    private String semanticQuery;
    
    @Schema(description = "用户当前位置纬度", example = "31.230416")
    private Double latitude;
    
    @Schema(description = "用户当前位置经度", example = "121.473701")
    private Double longitude;
    
    @Schema(description = "搜索半径（米）", example = "5000")
    private Integer radius;
    
    @Schema(description = "POI分类过滤", example = "美食")
    private String category;
    
    @Schema(description = "子分类列表", example = "[\"西餐\", \"日料\"]")
    private List<String> subCategories;
    
    @Schema(description = "价格区间：cheap/moderate/expensive/luxury", example = "moderate")
    private String priceLevel;
    
    @Schema(description = "最低评分", example = "4.0")
    private Double minRating;
    
    @Schema(description = "是否仅显示营业中", example = "true")
    private Boolean openNow;
    
    @Schema(description = "排序方式：distance/rating/price/relevance", example = "relevance")
    private String sortBy;
    
    @Schema(description = "分页页码", example = "1")
    private Integer page;
    
    @Schema(description = "每页数量", example = "20")
    private Integer pageSize;
    
    @Schema(description = "用户ID（用于个性化）", example = "user_123456")
    private String userId;
    
    @Schema(description = "搜索场景", example = "dining")
    private String scene;
    
    @Schema(description = "排除已访问过", example = "false")
    private Boolean excludeVisited;
    
    @Schema(description = "偏好标签", example = "[\"安静\", \"适合拍照\"]")
    private List<String> preferenceTags;
    
    @Schema(description = "扩展参数")
    private Map<String, Object> extraParams;
}
