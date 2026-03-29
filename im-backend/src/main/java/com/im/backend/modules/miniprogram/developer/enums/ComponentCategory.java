package com.im.backend.modules.miniprogram.developer.enums;

import lombok.Getter;

/**
 * 组件分类枚举
 */
@Getter
public enum ComponentCategory {
    BASIC("basic", "基础组件"),
    LAYOUT("layout", "布局组件"),
    FORM("form", "表单组件"),
    MEDIA("media", "媒体组件"),
    MAP("map", "地图组件"),
    PAYMENT("payment", "支付组件"),
    MARKETING("marketing", "营销组件"),
    SOCIAL("social", "社交组件"),
    COMMERCE("commerce", "电商组件");
    
    private final String code;
    private final String desc;
    
    ComponentCategory(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
