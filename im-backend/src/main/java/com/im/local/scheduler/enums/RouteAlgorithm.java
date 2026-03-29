package com.im.local.scheduler.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 路径优化算法枚举
 */
@Getter
@AllArgsConstructor
public enum RouteAlgorithm {
    
    GREEDY(1, "贪心算法", "Nearest Neighbor"),
    ANT_COLONY(2, "蚁群算法", "ACO"),
    GENETIC(3, "遗传算法", "GA"),
    SIMULATED_ANNEALING(4, "模拟退火", "SA"),
    DYNAMIC_PROGRAMMING(5, "动态规划", "DP");
    
    private final Integer code;
    private final String name;
    private final String algorithm;
    
    public static RouteAlgorithm fromCode(Integer code) {
        for (RouteAlgorithm algo : values()) {
            if (algo.code.equals(code)) {
                return algo;
            }
        }
        return GREEDY;
    }
}
