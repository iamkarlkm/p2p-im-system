package com.im.backend.enums;

/**
 * GeoHash精度级别枚举
 * 定义不同精度级别对应的网格大小
 * 
 * @author IM Development Team
 * @version 1.0
 */
public enum GeoHashPrecision {
    
    /**
     * 1级精度 - 约5000km
     */
    LEVEL_1(1, 5000000, "洲际级"),
    
    /**
     * 2级精度 - 约1250km
     */
    LEVEL_2(2, 1250000, "国家级"),
    
    /**
     * 3级精度 - 约156km
     */
    LEVEL_3(3, 156000, "省级"),
    
    /**
     * 4级精度 - 约39km
     */
    LEVEL_4(4, 39100, "市级"),
    
    /**
     * 5级精度 - 约4.9km
     */
    LEVEL_5(5, 4900, "区县级"),
    
    /**
     * 6级精度 - 约1.2km
     */
    LEVEL_6(6, 1200, "街道级"),
    
    /**
     * 7级精度 - 约150m
     */
    LEVEL_7(7, 150, "街区级"),
    
    /**
     * 8级精度 - 约20m
     */
    LEVEL_8(8, 20, "建筑级"),
    
    /**
     * 9级精度 - 约2.4m
     */
    LEVEL_9(9, 2, "精确级"),
    
    /**
     * 10级精度 - 约0.6m
     */
    LEVEL_10(10, 1, "高精度"),
    
    /**
     * 11级精度 - 约0.07m
     */
    LEVEL_11(11, 0, "超高精度"),
    
    /**
     * 12级精度 - 约0.02m
     */
    LEVEL_12(12, 0, "极限精度");
    
    /**
     * 精度级别
     */
    private final int level;
    
    /**
     * 网格大小（米）
     */
    private final int cellSize;
    
    /**
     * 描述
     */
    private final String description;
    
    GeoHashPrecision(int level, int cellSize, String description) {
        this.level = level;
        this.cellSize = cellSize;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getCellSize() {
        return cellSize;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据级别获取枚举
     */
    public static GeoHashPrecision getByLevel(int level) {
        for (GeoHashPrecision precision : values()) {
            if (precision.level == level) {
                return precision;
            }
        }
        return LEVEL_9; // 默认9级
    }
    
    /**
     * 根据距离（米）获取合适的精度级别
     */
    public static GeoHashPrecision getByDistance(double distanceMeters) {
        if (distanceMeters <= 5) return LEVEL_9;
        if (distanceMeters <= 20) return LEVEL_8;
        if (distanceMeters <= 150) return LEVEL_7;
        if (distanceMeters <= 1200) return LEVEL_6;
        if (distanceMeters <= 4900) return LEVEL_5;
        if (distanceMeters <= 39100) return LEVEL_4;
        if (distanceMeters <= 156000) return LEVEL_3;
        return LEVEL_2;
    }
    
    /**
     * 根据精度级别获取网格大小
     */
    public static int getCellSizeByLevel(int level) {
        GeoHashPrecision precision = getByLevel(level);
        return precision.getCellSize();
    }
    
    /**
     * 获取推荐精度（默认9级）
     */
    public static GeoHashPrecision getDefault() {
        return LEVEL_9;
    }
}
