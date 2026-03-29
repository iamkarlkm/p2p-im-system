package com.im.mapstream.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 聚合查询请求
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
public class ClusterQueryRequest {
    
    /** 左上角经度 */
    @NotNull
    private Double minLongitude;
    
    /** 左上角纬度 */
    @NotNull
    private Double maxLatitude;
    
    /** 右下角经度 */
    @NotNull
    private Double maxLongitude;
    
    /** 右下角纬度 */
    @NotNull
    private Double minLatitude;
    
    /** 地图缩放级别 */
    @NotNull
    private Integer zoom;
    
    /** 信息类型筛选 */
    private List<Integer> infoTypes;
    
    /** 最小聚合数量 */
    private Integer minClusterCount = 2;
    
    /** 时间范围(小时) */
    private Integer timeRangeHours = 24;
}
