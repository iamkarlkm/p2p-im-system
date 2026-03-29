package com.im.backend.modules.location.service;

/**
 * 地理围栏检测服务接口
 */
public interface IGeofenceDetectionService {

    /**
     * 检测位置与围栏的关系
     * @return 0-围栏外, 1-围栏内, -1-在边界上
     */
    int detectGeofence(Double lat, Double lng, Double centerLat, Double centerLng, Double radius);

    /**
     * 计算两点间距离(米)
     */
    double calculateDistance(Double lat1, Double lng1, Double lat2, Double lng2);

    /**
     * 检测点是否在多边形内(射线法)
     */
    boolean isPointInPolygon(Double lat, Double lng, Double[][] polygon);

    /**
     * 计算ETA(预计到达时间),单位:分钟
     */
    Integer calculateETA(Double currentLat, Double currentLng, Double destLat, Double destLng, Double speed);

    /**
     * 处理位置围栏事件
     */
    void processGeofenceEvent(String sessionId, Long userId, Double lat, Double lng);

    /**
     * 检查用户是否到达目的地
     */
    boolean checkArrival(String sessionId, Long userId, Double lat, Double lng);

    /**
     * 获取GeoHash值
     */
    String getGeoHash(Double lat, Double lng, Integer precision);
}
