package com.im.mapstream.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 热点发现实体
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotSpot {
    
    /** 热点ID */
    private String hotSpotId;
    
    /** 热点名称 */
    private String name;
    
    /** 热点描述 */
    private String description;
    
    /** 热点类型: EVENT/TRENDING/PLACE/TOPIC */
    private String hotSpotType;
    
    /** 中心经度 */
    private Double centerLongitude;
    
    /** 中心纬度 */
    private Double centerLatitude;
    
    /** 覆盖半径(米) */
    private Double coverageRadius;
    
    /** GeoHash网格列表 */
    private List<String> geohashGrids;
    
    /** 热度值 */
    private Double heatValue;
    
    /** 热度排名 */
    private Integer rank;
    
    /** 趋势: RISING/STABLE/FALLING */
    private String trend;
    
    /** 趋势变化率 */
    private Double trendRate;
    
    /** 相关信息流ID列表 */
    private List<String> relatedStreamIds;
    
    /** 相关内容预览 */
    private List<MapInfoStream> relatedStreams;
    
    /** 关键词标签 */
    private List<String> keywords;
    
    /** 参与用户数 */
    private Integer participantCount;
    
    /** 浏览总数 */
    private Long totalViews;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 发现时间 */
    private LocalDateTime discoverTime;
    
    /** 过期时间 */
    private LocalDateTime expireTime;
    
    /** 持续时间(小时) */
    private Integer durationHours;
    
    /** 状态: ACTIVE/COOLING/EXPIRED */
    private String status;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /**
     * 更新热度排名
     */
    public void updateRank(Integer newRank) {
        if (this.rank != null && newRank != null) {
            int rankChange = this.rank - newRank;
            if (rankChange > 0) {
                this.trend = "RISING";
            } else if (rankChange < 0) {
                this.trend = "FALLING";
            } else {
                this.trend = "STABLE";
            }
        }
        this.rank = newRank;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 是否活跃
     */
    public boolean isActive() {
        return "ACTIVE".equals(status) && 
               (expireTime == null || expireTime.isAfter(LocalDateTime.now()));
    }
    
    /**
     * 计算持续时间
     */
    public void calculateDuration() {
        if (createTime != null && expireTime != null) {
            this.durationHours = (int) java.time.Duration.between(
                createTime, expireTime).toHours();
        }
    }
}
