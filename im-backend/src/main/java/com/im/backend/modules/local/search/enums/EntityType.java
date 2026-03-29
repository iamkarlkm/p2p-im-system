package com.im.backend.modules.local.search.enums;

import lombok.Getter;

/**
 * 实体类型枚举
 */
@Getter
public enum EntityType {

    MERCHANT("MERCHANT", "商户", "具体商家实体"),
    CATEGORY("CATEGORY", "分类", "行业分类"),
    DISTRICT("DISTRICT", "商圈", "商业区域"),
    TAG("TAG", "标签", "特征标签"),
    BRAND("BRAND", "品牌", "连锁品牌"),
    LANDMARK("LANDMARK", "地标", "知名地点"),
    FACILITY("FACILITY", "设施", "公共设施");

    private final String code;
    private final String name;
    private final String description;

    EntityType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
