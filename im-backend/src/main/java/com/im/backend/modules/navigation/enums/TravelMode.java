package com.im.backend.modules.navigation.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 出行方式枚举
 */
@Getter
@AllArgsConstructor
public enum TravelMode {

    /**
     * 驾车
     */
    DRIVE("DRIVE", "驾车", "driving", "car"),

    /**
     * 步行
     */
    WALK("WALK", "步行", "walking", "foot"),

    /**
     * 骑行
     */
    RIDE("RIDE", "骑行", "bicycling", "bike"),

    /**
     * 公交
     */
    BUS("BUS", "公交", "transit", "bus"),

    /**
     * 货车
     */
    TRUCK("TRUCK", "货车", "truck", "truck");

    private final String code;
    private final String desc;
    private final String mapApiType;
    private final String icon;

    /**
     * 根据code获取枚举
     */
    public static TravelMode getByCode(String code) {
        for (TravelMode mode : values()) {
            if (mode.code.equalsIgnoreCase(code)) {
                return mode;
            }
        }
        return DRIVE;
    }

    /**
     * 是否支持实时路况
     */
    public boolean supportsTraffic() {
        return this == DRIVE || this == TRUCK;
    }

    /**
     * 是否支持路线策略选择
     */
    public boolean supportsStrategy() {
        return this == DRIVE || this == TRUCK;
    }

    /**
     * 是否计算费用
     */
    public boolean calculateCost() {
        return this == DRIVE || this == TRUCK || this == BUS;
    }

    /**
     * 获取默认速度(米/秒)
     */
    public int getDefaultSpeed() {
        switch (this) {
            case DRIVE:
                return 12; // ~43km/h
            case TRUCK:
                return 10; // ~36km/h
            case BUS:
                return 8;  // ~29km/h
            case RIDE:
                return 4;  // ~14km/h
            case WALK:
                return 1;  // ~3.6km/h
            default:
                return 12;
        }
    }

    /**
     * 获取最大建议距离(米)
     */
    public int getMaxRecommendedDistance() {
        switch (this) {
            case WALK:
                return 5000; // 5km
            case RIDE:
                return 20000; // 20km
            case BUS:
                return 50000; // 50km
            case DRIVE:
            case TRUCK:
                return 1000000; // 1000km
            default:
                return 50000;
        }
    }
}
