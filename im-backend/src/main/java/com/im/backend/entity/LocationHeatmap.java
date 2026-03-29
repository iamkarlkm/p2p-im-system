package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 位置热力图实体类
 * 用于存储位置热度分布数据
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@TableName("location_heatmap")
public class LocationHeatmap {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * GeoHash网格编码
     */
    private String geohash;
    
    /**
     * 网格精度级别
     */
    private Integer precision;
    
    /**
     * 中心纬度
     */
    private Double centerLat;
    
    /**
     * 中心经度
     */
    private Double centerLon;
    
    /**
     * 热度值
     */
    private Long heatValue;
    
    /**
     * 用户数量
     */
    private Integer userCount;
    
    /**
     * 位置点数量
     */
    private Integer pointCount;
    
    /**
     * POI数量
     */
    private Integer poiCount;
    
    /**
     * 活跃商家数量
     */
    private Integer merchantCount;
    
    /**
     * 数据类型：1-实时 2-小时 3-日 4-周 5-月
     */
    private Integer dataType;
    
    /**
     * 统计时间
     */
    private LocalDateTime statTime;
    
    /**
     * 时间戳（用于快速查询）
     */
    private Long timestamp;
    
    /**
     * 区域类型：1-住宅区 2-商业区 3-办公区 4-旅游区 5-交通枢纽
     */
    private Integer areaType;
    
    /**
     * 热力等级：1-冷 2-温 3-热 4-极热
     */
    private Integer heatLevel;
    
    /**
     * 峰值时段
     */
    private String peakHours;
    
    /**
     * 趋势：1-上升 2-平稳 3-下降
     */
    private Integer trend;
    
    /**
     * 同比变化率（百分比）
     */
    private Double yoyChange;
    
    /**
     * 环比变化率（百分比）
     */
    private Double momChange;
    
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
    public LocationHeatmap() {
        this.heatValue = 0L;
        this.userCount = 0;
        this.pointCount = 0;
        this.poiCount = 0;
        this.merchantCount = 0;
    }
    
    /**
     * 带参数构造
     */
    public LocationHeatmap(String geohash, Integer precision) {
        this();
        this.geohash = geohash;
        this.precision = precision;
    }
    
    /**
     * 增加热度值
     */
    public void addHeat(Long delta) {
        this.heatValue += delta;
        updateHeatLevel();
    }
    
    /**
     * 更新热力等级
     */
    public void updateHeatLevel() {
        if (heatValue < 100) {
            this.heatLevel = 1;
        } else if (heatValue < 500) {
            this.heatLevel = 2;
        } else if (heatValue < 2000) {
            this.heatLevel = 3;
        } else {
            this.heatLevel = 4;
        }
    }
    
    /**
     * 计算综合活跃度
     */
    public double calculateActivity() {
        return userCount * 0.4 + pointCount * 0.3 + poiCount * 0.2 + merchantCount * 0.1;
    }
    
    /**
     * 是否为热点区域
     */
    public boolean isHotspot() {
        return heatLevel >= 3 || heatValue > 1000;
    }
    
    /**
     * 获取热力强度描述
     */
    public String getHeatLevelDesc() {
        return switch (heatLevel) {
            case 1 -> "冷";
            case 2 -> "温";
            case 3 -> "热";
            case 4 -> "极热";
            default -> "未知";
        };
    }
    
    /**
     * 获取区域类型描述
     */
    public String getAreaTypeDesc() {
        return switch (areaType) {
            case 1 -> "住宅区";
            case 2 -> "商业区";
            case 3 -> "办公区";
            case 4 -> "旅游区";
            case 5 -> "交通枢纽";
            default -> "其他";
        };
    }
    
    /**
     * 合并热力数据
     */
    public void merge(LocationHeatmap other) {
        if (other == null) return;
        
        this.heatValue += other.heatValue;
        this.userCount += other.userCount;
        this.pointCount += other.pointCount;
        this.poiCount += other.poiCount;
        this.merchantCount += other.merchantCount;
        
        updateHeatLevel();
    }
}
