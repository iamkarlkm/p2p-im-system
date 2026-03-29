package com.im.backend.modules.local.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * 智能搜索请求DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "智能搜索请求")
public class SmartSearchRequest {
    
    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 500, message = "搜索关键词不能超过500字符")
    @Schema(description = "搜索关键词/自然语言查询", example = "附近好吃的火锅", required = true)
    private String query;
    
    @Schema(description = "用户当前经度", example = "121.4737")
    private Double longitude;
    
    @Schema(description = "用户当前纬度", example = "31.2304")
    private Double latitude;
    
    @Min(value = 100, message = "搜索半径最小100米")
    @Max(value = 50000, message = "搜索半径最大50公里")
    @Schema(description = "搜索半径（米），默认5000", example = "5000")
    @Builder.Default
    private Integer radius = 5000;
    
    @Schema(description = "POI类型筛选", example = "RESTAURANT")
    private String poiType;
    
    @Schema(description = "分类筛选", example = "火锅")
    private String category;
    
    @Min(value = 0)
    @Max(value = 5)
    @Schema(description = "最低评分", example = "4.0")
    private Double minRating;
    
    @Schema(description = "最高人均消费", example = "200")
    private Double maxPrice;
    
    @Schema(description = "最低人均消费", example = "50")
    private Double minPrice;
    
    @Schema(description = "排序方式: DISTANCE-距离, RATING-评分, POPULARITY-人气, PRICE_ASC-价格升序, PRICE_DESC-价格降序, SMART-智能排序", example = "SMART")
    @Builder.Default
    private String sortBy = "SMART";
    
    @Schema(description = "特色筛选", example = "[\"免费停车\", \"包间\"]")
    private List<String> features;
    
    @Schema(description = "场景标签: DATE-约会, FAMILY-家庭, BUSINESS-商务, FRIENDS-朋友聚会", example = "FAMILY")
    private String sceneTag;
    
    @Schema(description = "是否为语音搜索", example = "false")
    @Builder.Default
    private Boolean isVoice = false;
    
    @Schema(description = "方言类型", example = "mandarin")
    private String dialect;
    
    @Schema(description = "多轮对话会话ID", example = "conv_123456")
    private String conversationId;
    
    @Schema(description = "是否为多轮对话的后续查询", example = "false")
    @Builder.Default
    private Boolean isFollowUp = false;
    
    @Schema(description = "页码", example = "1")
    @Builder.Default
    private Integer pageNum = 1;
    
    @Schema(description = "每页数量", example = "20")
    @Builder.Default
    private Integer pageSize = 20;
    
    @Schema(description = "搜索来源: APP-移动应用, MINI_PROGRAM-小程序, H5-H5页面", example = "APP")
    @Builder.Default
    private String source = "APP";
    
    @Schema(description = "用户ID（系统自动填充）")
    private Long userId;
    
    // ==================== 业务方法 ====================
    
    /**
     * 构建缓存Key
     */
    public String buildCacheKey() {
        StringBuilder key = new StringBuilder("search:");
        key.append(query.hashCode()).append(":");
        if (longitude != null && latitude != null) {
            key.append(String.format("%.4f", longitude)).append(",").append(String.format("%.4f", latitude));
        }
        key.append(":").append(radius);
        if (poiType != null) key.append(":").append(poiType);
        if (category != null) key.append(":").append(category);
        if (sortBy != null) key.append(":").append(sortBy);
        return key.toString();
    }
    
    /**
     * 检查是否有地理位置
     */
    public boolean hasLocation() {
        return longitude != null && latitude != null;
    }
    
    /**
     * 计算分页偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
    
    /**
     * 获取价格区间描述
     */
    public String getPriceRange() {
        if (minPrice != null && maxPrice != null) {
            return String.format("%.0f-%.0f元", minPrice, maxPrice);
        } else if (minPrice != null) {
            return String.format("%.0f元以上", minPrice);
        } else if (maxPrice != null) {
            return String.format("%.0f元以下", maxPrice);
        }
        return null;
    }
}
