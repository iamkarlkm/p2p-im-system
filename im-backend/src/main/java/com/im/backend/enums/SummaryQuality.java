package com.im.backend.enums;

import lombok.Getter;

/**
 * 摘要质量枚举
 */
@Getter
public enum SummaryQuality {
    
    /**
     * 低质量 - 质量评分 < 60
     */
    LOW("LOW", "低质量"),
    
    /**
     * 中等质量 - 质量评分 60-79
     */
    MEDIUM("MEDIUM", "中等质量"),
    
    /**
     * 高质量 - 质量评分 80-89
     */
    HIGH("HIGH", "高质量"),
    
    /**
     * 优质 - 质量评分 >= 90
     */
    EXCELLENT("EXCELLENT", "优质");

    private final String code;
    private final String description;

    SummaryQuality(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static SummaryQuality fromCode(String code) {
        for (SummaryQuality quality : values()) {
            if (quality.code.equalsIgnoreCase(code)) {
                return quality;
            }
        }
        throw new IllegalArgumentException("未知的摘要质量: " + code);
    }

    /**
     * 根据质量评分获取质量枚举
     */
    public static SummaryQuality fromScore(Integer score) {
        if (score == null) {
            return MEDIUM;
        }
        if (score < 60) {
            return LOW;
        } else if (score < 80) {
            return MEDIUM;
        } else if (score < 90) {
            return HIGH;
        } else {
            return EXCELLENT;
        }
    }

    /**
     * 检查是否为可接受质量
     */
    public boolean isAcceptable() {
        return this != LOW;
    }

    /**
     * 检查是否为推荐质量
     */
    public boolean isRecommended() {
        return this == HIGH || this == EXCELLENT;
    }

    /**
     * 获取推荐的最小质量评分
     */
    public static Integer getRecommendedMinScore() {
        return 80;
    }

    /**
     * 获取可接受的最小质量评分
     */
    public static Integer getAcceptableMinScore() {
        return 60;
    }

    /**
     * 检查是否需要重新生成（质量过低）
     */
    public static boolean needsRegeneration(Integer score) {
        return score != null && score < getAcceptableMinScore();
    }

    /**
     * 获取质量等级对应的颜色（用于UI显示）
     */
    public String getColor() {
        switch (this) {
            case LOW: return "#ff4444"; // 红色
            case MEDIUM: return "#ffaa00"; // 橙色
            case HIGH: return "#00aa00"; // 绿色
            case EXCELLENT: return "#0088ff"; // 蓝色
            default: return "#999999";
        }
    }

    /**
     * 获取质量等级对应的图标（用于UI显示）
     */
    public String getIcon() {
        switch (this) {
            case LOW: return "⚠️";
            case MEDIUM: return "🔶";
            case HIGH: return "✅";
            case EXCELLENT: return "⭐";
            default: return "⚪";
        }
    }
}