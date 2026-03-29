package com.im.mapstream.enums;

/**
 * 热力状态枚举
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-30
 */
public enum HeatStatus {
    
    /** 低热度 */
    LOW(1, "LOW", "低热度", "#00FF00"),
    
    /** 中热度 */
    MEDIUM(2, "MEDIUM", "中热度", "#FFFF00"),
    
    /** 高热度 */
    HIGH(3, "HIGH", "高热度", "#FF8000"),
    
    /** 极高热度 */
    EXTREME(4, "EXTREME", "极高热度", "#FF0000"),
    
    /** 冷却中 */
    COOLING(5, "COOLING", "冷却中", "#808080");
    
    private final int level;
    private final String name;
    private final String description;
    private final String color;
    
    HeatStatus(int level, String name, String description, String color) {
        this.level = level;
        this.name = name;
        this.description = description;
        this.color = color;
    }
    
    public int getLevel() { return level; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getColor() { return color; }
    
    /**
     * 根据热度值获取状态
     */
    public static HeatStatus fromHeatValue(double heatValue) {
        if (heatValue < 10) return LOW;
        if (heatValue < 50) return MEDIUM;
        if (heatValue < 100) return HIGH;
        return EXTREME;
    }
}
