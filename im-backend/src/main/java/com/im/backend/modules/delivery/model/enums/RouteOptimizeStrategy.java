package com.im.backend.modules.delivery.model.enums;

import lombok.Getter;

/**
 * 路径优化策略
 */
@Getter
public enum RouteOptimizeStrategy {
    
    SHORTEST_DISTANCE(1, "最短距离", "优先选择总距离最短的路线"),
    FASTEST_TIME(2, "最快时间", "考虑实时路况选择最快路线"),
    LEAST_TRAFFIC(3, "最少拥堵", "避开拥堵路段"),
    BALANCED(4, "均衡模式", "距离与时间的平衡"),
    FUEL_EFFICIENT(5, "节能模式", "减少急加速急减速"),
    PRIORITY_BASED(6, "优先级", "按订单优先级排序");
    
    private final int code;
    private final String name;
    private final String desc;
    
    RouteOptimizeStrategy(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }
}
