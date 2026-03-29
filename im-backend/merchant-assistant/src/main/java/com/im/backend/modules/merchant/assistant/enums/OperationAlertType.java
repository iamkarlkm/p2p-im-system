package com.im.backend.modules.merchant.assistant.enums;

/**
 * 运营提醒类型枚举
 */
public enum OperationAlertType {
    ORDER_ABNORMAL("ORDER_ABNORMAL", "订单异常提醒"),
    INVENTORY_LOW("INVENTORY_LOW", "库存不足提醒"),
    COMPETITOR_PRICE("COMPETITOR_PRICE", "竞品价格监控"),
    DAILY_REPORT("DAILY_REPORT", "经营日报"),
    WEEKLY_REPORT("WEEKLY_REPORT", "经营周报"),
    MONTHLY_REPORT("MONTHLY_REPORT", "经营月报"),
    REVIEW_NEGATIVE("REVIEW_NEGATIVE", "差评预警"),
    SALES_DROP("SALES_DROP", "销量下滑预警"),
    NEW_ORDER("NEW_ORDER", "新订单提醒"),
    DELIVERY_TIMEOUT("DELIVERY_TIMEOUT", "配送超时预警");
    
    private final String code;
    private final String desc;
    
    OperationAlertType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}
