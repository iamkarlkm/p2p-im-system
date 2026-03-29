package com.im.backend.dto.search;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 语义搜索请求DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemanticSearchRequestDTO {
    
    /**
     * 用户自然语言查询
     */
    @NotBlank(message = "查询内容不能为空")
    private String query;
    
    /**
     * 会话ID（多轮对话时使用）
     */
    private String sessionId;
    
    /**
     * 用户当前经度
     */
    @NotNull(message = "经度不能为空")
    private Double longitude;
    
    /**
     * 用户当前纬度
     */
    @NotNull(message = "纬度不能为空")
    private Double latitude;
    
    /**
     * 城市代码
     */
    private String cityCode;
    
    /**
     * 区县代码
     */
    private String districtCode;
    
    /**
     * 是否为语音输入
     */
    private Boolean isVoiceInput;
    
    /**
     * 语音数据（Base64编码）
     */
    private String voiceData;
    
    /**
     * 搜索范围（米）
     */
    private Integer searchRadius;
    
    /**
     * 页码
     */
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 排序方式
     * DISTANCE: 距离优先
     * RATING: 评分优先
     * POPULARITY: 人气优先
     * SMART: 智能排序
     */
    private String sortBy;
    
    /**
     * 过滤器
     */
    private SearchFilterDTO filters;
    
    /**
     * 设备类型
     */
    private String deviceType;
    
    /**
     * 获取页码（带默认值）
     */
    public Integer getPageNum() {
        return pageNum != null && pageNum > 0 ? pageNum : 1;
    }
    
    /**
     * 获取每页大小（带默认值）
     */
    public Integer getPageSize() {
        return pageSize != null && pageSize > 0 ? Math.min(pageSize, 50) : 20;
    }
    
    /**
     * 搜索过滤器DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchFilterDTO {
        /**
         * POI分类
         */
        private List<String> categories;
        
        /**
         * 最低评分
         */
        private Double minRating;
        
        /**
         * 最高人均消费
         */
        private Double maxPrice;
        
        /**
         * 最低人均消费
         */
        private Double minPrice;
        
        /**
         * 营业状态
         * OPEN: 营业中
         * ALL: 全部
         */
        private String businessStatus;
        
        /**
         * 特色标签
         */
        private List<String> tags;
        
        /**
         * 是否有优惠
         */
        private Boolean hasDiscount;
        
        /**
         * 是否支持预订
         */
        private Boolean supportReservation;
    }
}
