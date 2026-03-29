package com.im.entity.delivery;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手位置记录实体类 - 即时配送运力调度系统
 * 存储骑手历史位置轨迹
 */
@Data
public class RiderLocation {
    
    /** 位置记录ID */
    private Long id;
    
    /** 骑手ID */
    private Long riderId;
    
    /** 经度 */
    private BigDecimal longitude;
    
    /** 纬度 */
    private BigDecimal latitude;
    
    /** 精度(米) */
    private BigDecimal accuracy;
    
    /** 海拔高度(米) */
    private BigDecimal altitude;
    
    /** 速度(m/s) */
    private BigDecimal speed;
    
    /** 方向(0-360度) */
    private BigDecimal direction;
    
    /** 定位时间 */
    private LocalDateTime locationTime;
    
    /** 数据来源: GPS-卫星定位, WIFI-WiFi定位, CELL-基站定位, HYBRID-混合定位 */
    private String source;
    
    /** 电池电量(%) */
    private Integer batteryLevel;
    
    /** 网络类型: 4G, 5G, WIFI */
    private String networkType;
    
    /** 配送订单ID(配送中时关联) */
    private Long deliveryOrderId;
    
    /** 当前订单状态 */
    private String orderStatus;
    
    /** 配送区域ID */
    private Long zoneId;
    
    /** 地理哈希(GeoHash) */
    private String geoHash;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    // ========== 业务方法 ==========
    
    /**
     * 计算与另一个位置点的距离(米)
     */
    public double distanceTo(RiderLocation other) {
        return calculateDistance(
            latitude.doubleValue(), longitude.doubleValue(),
            other.latitude.doubleValue(), other.longitude.doubleValue()
        );
    }
    
    /**
     * 计算两点间距离(米) - Haversine公式
     */
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371000;
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(radLat1) * Math.cos(radLat2) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * 生成GeoHash编码
     */
    public void generateGeoHash(int precision) {
        this.geoHash = encodeGeoHash(latitude.doubleValue(), longitude.doubleValue(), precision);
    }
    
    /**
     * GeoHash编码实现
     */
    private String encodeGeoHash(double lat, double lng, int precision) {
        String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
        double latMin = -90, latMax = 90;
        double lngMin = -180, lngMax = 180;
        StringBuilder geohash = new StringBuilder();
        boolean isEven = true;
        int bit = 0, ch = 0;
        
        while (geohash.length() < precision) {
            if (isEven) {
                double lngMid = (lngMin + lngMax) / 2;
                if (lng >= lngMid) {
                    ch |= (1 << (4 - bit));
                    lngMin = lngMid;
                } else {
                    lngMax = lngMid;
                }
            } else {
                double latMid = (latMin + latMax) / 2;
                if (lat >= latMid) {
                    ch |= (1 << (4 - bit));
                    latMin = latMid;
                } else {
                    latMax = latMid;
                }
            }
            
            isEven = !isEven;
            if (bit < 4) {
                bit++;
            } else {
                geohash.append(base32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }
        
        return geohash.toString();
    }
    
    /**
     * 检查位置是否有效
     */
    public boolean isValid() {
        if (latitude == null || longitude == null) return false;
        if (latitude.doubleValue() < -90 || latitude.doubleValue() > 90) return false;
        if (longitude.doubleValue() < -180 || longitude.doubleValue() > 180) return false;
        if (accuracy != null && accuracy.doubleValue() > 1000) return false; // 精度大于1km认为无效
        return true;
    }
    
    /**
     * 检查是否为运动状态
     */
    public boolean isMoving() {
        return speed != null && speed.doubleValue() > 0.5; // 速度大于0.5m/s认为在移动
    }
}
