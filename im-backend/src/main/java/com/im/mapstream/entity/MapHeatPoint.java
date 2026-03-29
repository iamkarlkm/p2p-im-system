package com.im.mapstream.entity;

import com.im.mapstream.enums.HeatStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 地图热力点实体
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MapHeatPoint {
    
    /** 热力点ID */
    private String heatPointId;
    
    /** GeoHash编码 */
    private String geohash;
    
    /** GeoHash精度 */
    private Integer geohashLength;
    
    /** 聚合中心经度 */
    private Double centerLongitude;
    
    /** 聚合中心纬度 */
    private Double centerLatitude;
    
    /** 聚合的信息流数量 */
    private Integer streamCount;
    
    /** 用户数量 */
    private Integer userCount;
    
    /** 热度值(聚合计算) */
    private Double heatValue;
    
    /** 热力状态 */
    private HeatStatus heatStatus;
    
    /** 信息类型分布统计 */
    private java.util.Map<String, Integer> typeDistribution;
    
    /** 时间窗口起始 */
    private LocalDateTime timeWindowStart;
    
    /** 时间窗口结束 */
    private LocalDateTime timeWindowEnd;
    
    /** 聚合时间戳 */
    private LocalDateTime aggregationTime;
    
    /** 数据版本 */
    private Integer version;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /**
     * 增加热度值
     */
    public void addHeat(double delta) {
        this.heatValue = (this.heatValue == null ? 0.0 : this.heatValue) + delta;
        updateStatus();
    }
    
    /**
     * 更新状态
     */
    public void updateStatus() {
        this.heatStatus = HeatStatus.fromHeatValue(this.heatValue);
    }
    
    /**
     * 合并另一个热力点
     */
    public void merge(MapHeatPoint other) {
        this.streamCount += other.streamCount;
        this.userCount += other.userCount;
        this.heatValue += other.heatValue;
        
        // 合并类型分布
        if (other.typeDistribution != null) {
            other.typeDistribution.forEach((type, count) -> 
                this.typeDistribution.merge(type, count, Integer::sum)
            );
        }
        updateStatus();
    }
}
