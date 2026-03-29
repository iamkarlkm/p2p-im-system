package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * GeoHash网格实体类
 * 用于存储GeoHash空间索引网格信息
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@TableName("geohash_grid")
public class GeoHashGrid {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * GeoHash编码
     */
    private String geohash;
    
    /**
     * 网格精度级别（1-12）
     */
    private Integer precision;
    
    /**
     * 网格中心纬度
     */
    private Double centerLat;
    
    /**
     * 网格中心经度
     */
    private Double centerLon;
    
    /**
     * 网格边界最小纬度
     */
    private Double minLat;
    
    /**
     * 网格边界最大纬度
     */
    private Double maxLat;
    
    /**
     * 网格边界最小经度
     */
    private Double minLon;
    
    /**
     * 网格边界最大经度
     */
    private Double maxLon;
    
    /**
     * 网格内位置点数量
     */
    private Integer pointCount;
    
    /**
     * 网格热度（访问频次）
     */
    private Long heatScore;
    
    /**
     * 网格类型：1-普通区域 2-热点区域 3-商业区
     */
    private Integer gridType;
    
    /**
     * 父级GeoHash（更高精度）
     */
    private String parentGeohash;
    
    /**
     * 子级GeoHash列表（逗号分隔）
     */
    private String childGeohashes;
    
    /**
     * 网格内POI数量
     */
    private Integer poiCount;
    
    /**
     * 最近活跃时间
     */
    private LocalDateTime lastActiveTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
    
    /**
     * 无参构造
     */
    public GeoHashGrid() {
        this.pointCount = 0;
        this.heatScore = 0L;
        this.poiCount = 0;
    }
    
    /**
     * 带参数构造
     */
    public GeoHashGrid(String geohash, Integer precision) {
        this();
        this.geohash = geohash;
        this.precision = precision;
    }
    
    /**
     * 增加位置点计数
     */
    public void incrementPointCount() {
        this.pointCount++;
        this.heatScore++;
        this.lastActiveTime = LocalDateTime.now();
    }
    
    /**
     * 减少位置点计数
     */
    public void decrementPointCount() {
        if (this.pointCount > 0) {
            this.pointCount--;
        }
        this.lastActiveTime = LocalDateTime.now();
    }
    
    /**
     * 增加热度
     */
    public void addHeat(Long delta) {
        this.heatScore += delta;
    }
    
    /**
     * 获取网格面积（平方米）
     * 简化计算，实际应根据经纬度范围计算
     */
    public double getArea() {
        if (minLat == null || maxLat == null || minLon == null || maxLon == null) {
            return 0.0;
        }
        
        double latDiff = maxLat - minLat;
        double lonDiff = maxLon - minLon;
        
        // 近似计算（每度纬度约111km，经度根据纬度变化）
        double latMeters = latDiff * 111000;
        double lonMeters = lonDiff * 111000 * Math.cos(Math.toRadians(centerLat));
        
        return latMeters * lonMeters;
    }
    
    /**
     * 获取网格密度（点/平方公里）
     */
    public double getDensity() {
        double area = getArea() / 1000000; // 转换为平方公里
        return area > 0 ? pointCount / area : 0;
    }
    
    /**
     * 判断网格是否为热点
     */
    public boolean isHotGrid() {
        return heatScore > 1000 || pointCount > 50;
    }
}
