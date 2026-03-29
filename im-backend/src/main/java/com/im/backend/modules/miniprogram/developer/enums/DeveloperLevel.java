package com.im.backend.modules.miniprogram.developer.enums;

import lombok.Getter;

/**
 * 开发者等级枚举
 */
@Getter
public enum DeveloperLevel {
    JUNIOR(1, "初级开发者", 0),
    INTERMEDIATE(2, "中级开发者", 1000),
    ADVANCED(3, "高级开发者", 5000),
    EXPERT(4, "专家开发者", 20000);
    
    private final Integer level;
    private final String desc;
    private final Integer minPoints;
    
    DeveloperLevel(Integer level, String desc, Integer minPoints) {
        this.level = level;
        this.desc = desc;
        this.minPoints = minPoints;
    }
}
