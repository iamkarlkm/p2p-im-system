package com.im.backend.modules.bi.enums;

import lombok.Getter;

/**
 * RFM用户价值分层枚举
 */
@Getter
public enum RfmSegment {

    CHAMPIONS("champions", "重要价值客户", "消费金额高、频次高、最近消费"),
    LOYAL_CUSTOMERS("loyal", "重要保持客户", "消费金额高、频次高、但近期未消费"),
    POTENTIAL_LOYALISTS("potential", "重要发展客户", "最近消费、频次中等"),
    NEW_CUSTOMERS("new", "新客户", "最近首次消费"),
    PROMISING("promising", "潜力客户", "最近消费但金额较低"),
    NEED_ATTENTION("attention", "需要关注", "消费频次下降"),
    ABOUT_TO_SLEEP("sleepy", "即将流失", "很久未消费"),
    AT_RISK("risk", "流失风险", "曾经高频高消费但现在很少消费"),
    LOST("lost", "已流失", "长期未消费");

    private final String code;
    private final String name;
    private final String description;

    RfmSegment(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
}
