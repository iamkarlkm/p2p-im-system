package com.im.backend.modules.local.search.enums;

import lombok.Getter;

/**
 * 热度趋势枚举
 */
@Getter
public enum HotTrend {

    UP("UP", "上升", "热度上升"),
    DOWN("DOWN", "下降", "热度下降"),
    STABLE("STABLE", "稳定", "热度稳定"),
    NEW("NEW", "新增", "新增热词"),
    EXPLOSIVE("EXPLOSIVE", "爆增", "热度爆增");

    private final String code;
    private final String name;
    private final String description;

    HotTrend(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
