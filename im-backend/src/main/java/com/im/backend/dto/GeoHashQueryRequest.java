package com.im.backend.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * GeoHash查询请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
public class GeoHashQueryRequest {
    
    /**
     * GeoHash编码
     */
    @NotBlank(message = "GeoHash不能为空")
    private String geohash;
    
    /**
     * 查询精度（覆盖GeoHash长度）
     */
    @Min(1)
    @Max(12)
    private Integer precision;
    
    /**
     * 是否包含邻居网格
     */
    private Boolean includeNeighbors = false;
    
    /**
     * 查询类型：1-位置点 2-POI 3-热力 4-全部
     */
    @Min(1)
    @Max(4)
    private Integer queryType = 4;
    
    /**
     * 页码
     */
    @Min(1)
    private Integer pageNum = 1;
    
    /**
     * 每页数量
     */
    @Min(1)
    @Max(500)
    private Integer pageSize = 100;
    
    /**
     * 是否统计网格热度
     */
    private Boolean calcHeat = false;
}
