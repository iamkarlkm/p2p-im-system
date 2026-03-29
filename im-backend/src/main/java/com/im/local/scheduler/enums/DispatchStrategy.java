package com.im.local.scheduler.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 调度策略枚举
 */
@Getter
@AllArgsConstructor
public enum DispatchStrategy {
    
    NEAREST(1, "最近优先", "距离最近骑手优先"),
    LOAD_BALANCE(2, "负载均衡", "均匀分配订单"),
    CAPACITY(3, "运力优先", "剩余运力最多优先"),
    RATING(4, "评分优先", "骑手评分优先"),
    SMART(5, "智能调度", "多因子综合评分");
    
    private final Integer code;
    private final String name;
    private final String description;
    
    public static DispatchStrategy fromCode(Integer code) {
        for (DispatchStrategy strategy : values()) {
            if (strategy.code.equals(code)) {
                return strategy;
            }
        }
        return SMART;
    }
}
