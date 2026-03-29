package com.im.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 位置点实体类
 * 用于存储用户、设备或POI的精确位置信息
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Data
@TableName("location_point")
public class LocationPoint {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 位置点唯一标识
     */
    private String pointId;
    
    /**
     * 关联用户ID
     */
    private Long userId;
    
    /**
     * 位置类型：1-用户位置 2-商家位置 3-设备位置 4-POI位置
     */
    private Integer locationType;
    
    /**
     * 纬度
     */
    private Double latitude;
    
    /**
     * 经度
     */
    private Double longitude;
    
    /**
     * 海拔高度（米）
     */
    private Double altitude;
    
    /**
     * 精度（米）
     */
    private Double accuracy;
    
    /**
     * GeoHash编码（9级精度）
     */
    private String geohash;
    
    /**
     * 国家
     */
    private String country;
    
    /**
     * 省份
     */
    private String province;
    
    /**
     * 城市
     */
    private String city;
    
    /**
     * 区县
     */
    private String district;
    
    /**
     * 街道地址
     */
    private String street;
    
    /**
     * 详细地址
     */
    private String address;
    
    /**
     * 速度（米/秒）
     */
    private Double speed;
    
    /**
     * 方向（度，0-360）
     */
    private Double direction;
    
    /**
     * 位置来源：1-GPS 2-WiFi 3-基站 4-IP定位
     */
    private Integer sourceType;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * 定位时间
     */
    private LocalDateTime locationTime;
    
    /**
     * 过期时间（用于临时位置）
     */
    private LocalDateTime expireTime;
    
    /**
     * 是否共享位置
     */
    private Boolean isShared;
    
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
     * 临时字段：距离查询结果（不入库）
     */
    @TableField(exist = false)
    private Double distance;
    
    /**
     * 无参构造
     */
    public LocationPoint() {
        this.isShared = true;
        this.sourceType = 1;
    }
    
    /**
     * 带经纬度构造
     */
    public LocationPoint(Double latitude, Double longitude) {
        this();
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    /**
     * 完整构造
     */
    public LocationPoint(Long userId, Double latitude, Double longitude, Integer locationType) {
        this(latitude, longitude);
        this.userId = userId;
        this.locationType = locationType;
        this.pointId = generatePointId();
    }
    
    /**
     * 生成位置点ID
     */
    private String generatePointId() {
        return "LOC" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }
    
    /**
     * 计算距离（米）
     */
    public double calculateDistanceTo(LocationPoint other) {
        return haversineDistance(this.latitude, this.longitude, other.latitude, other.longitude);
    }
    
    /**
     * Haversine距离计算
     */
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000;
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 判断位置是否有效
     */
    public boolean isValid() {
        return latitude != null && longitude != null &&
               latitude >= -90 && latitude <= 90 &&
               longitude >= -180 && longitude <= 180;
    }
    
    /**
     * 判断位置是否过期
     */
    public boolean isExpired() {
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * 获取坐标字符串
     */
    public String getCoordinateString() {
        return String.format("%.6f,%.6f", latitude, longitude);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationPoint that = (LocationPoint) o;
        return pointId != null && pointId.equals(that.pointId);
    }
    
    @Override
    public int hashCode() {
        return pointId != null ? pointId.hashCode() : 0;
    }
}
