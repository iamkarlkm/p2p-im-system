package com.im.local.scheduler.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 围栏饱和度等级枚举
 */
@Getter
@AllArgsConstructor
public enum SaturationLevel {
    
    LOW(0, 30, "低饱和度", "运力充足", "#52c41a"),
    MEDIUM(30, 70, "中等饱和度", "运力正常", "#faad14"),
    HIGH(70, 90, "高饱和度", "运力紧张", "#fa8c16"),
    OVERLOAD(90, 100, "超负荷", "运力严重不足", "#f5222d");
    
    private final int minRate;
    private final int maxRate;
    private final String name;
    private final String description;
    private final String color;
    
    public static SaturationLevel fromRate(int rate) {
        for (SaturationLevel level : values()) {
            if (rate >= level.minRate && rate < level.maxRate) {
                return level;
            }
        }
        return OVERLOAD;
    }
}
