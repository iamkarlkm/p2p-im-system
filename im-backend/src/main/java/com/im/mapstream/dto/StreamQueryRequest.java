package com.im.mapstream.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 信息流查询请求
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
public class StreamQueryRequest {
    
    @NotNull(message = "经度不能为空")
    private Double longitude;
    
    @NotNull(message = "纬度不能为空")
    private Double latitude;
    
    /** 搜索半径(米),默认5000 */
    private Integer radius = 5000;
    
    /** 信息类型筛选 */
    private List<Integer> infoTypes;
    
    /** 关键字搜索 */
    private String keyword;
    
    /** 标签筛选 */
    private List<String> tags;
    
    /** 排序: HEAT/TIME/DISTANCE */
    private String sortBy = "HEAT";
    
    /** 页码 */
    private Integer pageNum = 1;
    
    /** 每页数量 */
    private Integer pageSize = 20;
    
    /** 最小热度值 */
    private Double minHeatValue;
    
    /** 时间范围(小时) */
    private Integer timeRangeHours = 24;
}
