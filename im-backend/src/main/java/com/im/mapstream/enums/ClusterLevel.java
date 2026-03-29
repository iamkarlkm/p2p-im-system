package com.im.mapstream.enums;

/**
 * 地图聚合层级枚举
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
public enum ClusterLevel {
    
    /** 全球级 - GeoHash 2位 */
    WORLD(2, "世界", 10000000.0),
    
    /** 国家级 - GeoHash 3位 */
    COUNTRY(3, "国家", 1000000.0),
    
    /** 城市级 - GeoHash 4位 */
    CITY(4, "城市", 100000.0),
    
    /** 区县级 - GeoHash 5位 */
    DISTRICT(5, "区县", 10000.0),
    
    /** 街道级 - GeoHash 6位 */
    STREET(6, "街道", 1000.0),
    
    /** 商圈级 - GeoHash 7位 */
    BUSINESS(7, "商圈", 100.0),
    
    /** POI级 - GeoHash 8位 */
    POI(8, "POI", 10.0),
    
    /** 精确级 - GeoHash 9位 */
    PRECISE(9, "精确", 1.0);
    
    private final int geohashLength;
    private final String description;
    private final double approximateRadius; // 米
    
    ClusterLevel(int geohashLength, String description, double approximateRadius) {
        this.geohashLength = geohashLength;
        this.description = description;
        this.approximateRadius = approximateRadius;
    }
    
    public int getGeohashLength() { return geohashLength; }
    public String getDescription() { return description; }
    public double getApproximateRadius() { return approximateRadius; }
    
    /**
     * 根据缩放级别获取聚合层级
     */
    public static ClusterLevel fromZoom(int zoom) {
        if (zoom <= 3) return WORLD;
        if (zoom <= 5) return COUNTRY;
        if (zoom <= 7) return CITY;
        if (zoom <= 9) return DISTRICT;
        if (zoom <= 11) return STREET;
        if (zoom <= 13) return BUSINESS;
        if (zoom <= 15) return POI;
        return PRECISE;
    }
}
