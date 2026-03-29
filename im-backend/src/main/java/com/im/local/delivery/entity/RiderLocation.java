package com.im.local.delivery.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手位置轨迹点
 * 用于记录骑手实时位置和配送轨迹
 */
@Data
public class RiderLocation {
    
    /** 轨迹点ID */
    private Long id;
    
    /** 骑手ID */
    private Long riderId;
    
    /** 配送订单ID */
    private Long deliveryOrderId;
    
    /** 纬度 */
    private BigDecimal lat;
    
    /** 经度 */
    private BigDecimal lng;
    
    /** 精度(米) */
    private Double accuracy;
    
    /** 海拔(米) */
    private Double altitude;
    
    /** 速度(m/s) */
    private Double speed;
    
    /** 方向(0-360度) */
    private Double direction;
    
    /** 定位时间 */
    private LocalDateTime locatedAt;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** GeoHash索引 */
    private String geoHash;
    
    /** 地址描述 */
    private String address;
    
    /** 定位类型：1-GPS, 2-网络, 3-基站 */
    private Integer locationType;
    
    /** 电池电量(%) */
    private Integer batteryLevel;
    
    /** 是否Mock位置 */
    private Boolean isMock;
    
    /**
     * 计算两点间距离(米)
     */
    public double distanceTo(RiderLocation other) {
        if (other == null || this.lat == null || this.lng == null 
            || other.getLat() == null || other.getLng() == null) {
            return Double.MAX_VALUE;
        }
        return haversineDistance(
            this.lat.doubleValue(), this.lng.doubleValue(),
            other.getLat().doubleValue(), other.getLng().doubleValue()
        );
    }
    
    /**
     * Haversine公式计算球面距离
     */
    private double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        final double R = 6371000; // 地球半径(米)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    /**
     * 转换为GeoHash
     */
    public String calculateGeoHash(int precision) {
        if (lat == null || lng == null) {
            return null;
        }
        return encodeGeoHash(lat.doubleValue(), lng.doubleValue(), precision);
    }
    
    private String encodeGeoHash(double lat, double lng, int precision) {
        String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
        double[] latRange = {-90.0, 90.0};
        double[] lngRange = {-180.0, 180.0};
        StringBuilder geohash = new StringBuilder();
        boolean isEven = true;
        int bit = 0;
        int ch = 0;
        
        while (geohash.length() < precision) {
            if (isEven) {
                double mid = (lngRange[0] + lngRange[1]) / 2.0;
                if (lng >= mid) {
                    ch |= (1 << (4 - bit));
                    lngRange[0] = mid;
                } else {
                    lngRange[1] = mid;
                }
            } else {
                double mid = (latRange[0] + latRange[1]) / 2.0;
                if (lat >= mid) {
                    ch |= (1 << (4 - bit));
                    latRange[0] = mid;
                } else {
                    latRange[1] = mid;
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
}
