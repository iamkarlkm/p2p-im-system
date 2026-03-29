package com.im.backend.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 附近POI查询请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class NearbyPoiRequest {
    
    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    @Min(value = -90, message = "纬度范围-90~90")
    @Max(value = 90, message = "纬度范围-90~90")
    private Double latitude;
    
    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    @Min(value = -180, message = "经度范围-180~180")
    @Max(value = 180, message = "经度范围-180~180")
    private Double longitude;
    
    /**
     * 查询半径（米），默认1000米
     */
    @Min(value = 1, message = "半径至少1米")
    @Max(value = 50000, message = "半径最大50公里")
    private Integer radius = 1000;
    
    /**
     * POI分类编码
     */
    private String categoryCode;
    
    /**
     * 关键字搜索
     */
    private String keyword;
    
    /**
     * 排序方式：1-距离 2-评分 3-人气 4-综合
     */
    @Min(1)
    @Max(4)
    private Integer sortBy = 1;
    
    /**
     * 页码
     */
    @Min(1)
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    @Min(1)
    @Max(100)
    private Integer pageSize = 20;
    
    /**
     * 是否只显示营业中
     */
    private Boolean openOnly = false;
    
    /**
     * 最小评分
     */
    @Min(1)
    @Max(5)
    private Double minRating;
    
    /**
     * 最大平均消费
     */
    private Double maxPrice;
    
    /**
     * 用户ID（用于个性化推荐）
     */
    private Long userId;
    
    /**
     * 是否使用缓存
     */
    private Boolean useCache = true;
}
