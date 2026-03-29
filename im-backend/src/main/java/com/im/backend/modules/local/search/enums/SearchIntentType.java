package com.im.backend.modules.local.search.enums;

import lombok.Getter;

/**
 * 搜索意图类型枚举
 */
@Getter
public enum SearchIntentType {

    NAVIGATION("NAVIGATION", "导航意图", "查找地点并导航前往"),
    DISCOVERY("DISCOVERY", "发现意图", "探索附近好去处"),
    COMPARISON("COMPARISON", "对比意图", "比较多个商家或商品"),
    PRICE_QUERY("PRICE_QUERY", "价格查询", "查询价格信息"),
    RESERVATION("RESERVATION", "预约意图", "预约服务或订位"),
    GROUP_BUY("GROUP_BUY", "团购意图", "寻找优惠信息"),
    DELIVERY("DELIVERY", "外卖意图", "点外卖或配送服务"),
    REVIEW("REVIEW", "评价意图", "查看评价口碑"),
    INFORMATION("INFORMATION", "信息意图", "获取商家信息"),
    SOCIAL("SOCIAL", "社交意图", "寻找社交场所"),
    GENERAL("GENERAL", "通用意图", "一般性搜索");

    private final String code;
    private final String name;
    private final String description;

    SearchIntentType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
