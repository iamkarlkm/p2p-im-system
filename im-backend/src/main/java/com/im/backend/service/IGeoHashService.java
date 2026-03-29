package com.im.backend.service;

import com.im.backend.dto.*;
import com.im.backend.entity.GeoHashGrid;
import com.im.backend.entity.LocationHeatmap;

import java.util.List;
import java.util.Map;

/**
 * GeoHash服务接口
 * 提供空间索引和网格管理功能
 * 
 * @author IM Development Team
 * @version 1.0
 */
public interface IGeoHashService {
    
    /**
     * 编码经纬度为GeoHash
     */
    String encode(double latitude, double longitude, int precision);
    
    /**
     * 解码GeoHash为坐标
     */
    double[] decode(String geohash);
    
    /**
     * 获取邻居网格
     */
    String[] getNeighbors(String geohash);
    
    /**
     * 计算两点距离
     */
    double calculateDistance(String geohash1, String geohash2);
    
    /**
     * 创建或更新网格
     */
    GeoHashGrid createOrUpdateGrid(String geohash, int precision);
    
    /**
     * 获取网格信息
     */
    GeoHashGrid getGridInfo(String geohash);
    
    /**
     * 查询网格列表
     */
    List<GeoHashGrid> queryGrids(GeoHashQueryRequest request);
    
    /**
     * 获取热门网格
     */
    List<GeoHashGrid> getHotGrids(int topN);
    
    /**
     * 获取网格热度统计
     */
    Map<String, Object> getGridStatistics();
    
    /**
     * 获取网格热力图数据
     */
    List<LocationHeatmap> getHeatmapData(String geohash, int precision);
    
    /**
     * 刷新网格数据
     */
    void refreshGridData(String geohash);
    
    /**
     * 批量刷新网格
     */
    void batchRefreshGrids(List<String> geohashes);
    
    /**
     * 获取网格内的POI数量
     */
    Long countPoisInGrid(String geohash);
    
    /**
     * 获取网格内的用户数量
     */
    Long countUsersInGrid(String geohash);
    
    /**
     * 根据距离获取推荐精度
     */
    int getRecommendedPrecision(double distanceMeters);
}
