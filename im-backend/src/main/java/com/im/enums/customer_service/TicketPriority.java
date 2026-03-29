package com.im.enums.customer_service;

import lombok.Getter;

/**
 * 工单优先级枚举
 * 功能 #319 - 智能客服与工单管理系统
 */
@Getter
public enum TicketPriority {
    
    LOW(1, "低", "普通咨询，可延后处理"),
    MEDIUM(2, "中", "一般问题，正常处理"),
    HIGH(3, "高", "重要问题，优先处理"),
    URGENT(4, "紧急", "紧急问题，立即处理");
    
    private final int code;
    private final String name;
    private final String desc;
    
    TicketPriority(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }
    
    public static TicketPriority fromCode(int code) {
        for (TicketPriority priority : values()) {
            if (priority.code == code) {
                return priority;
            }
        }
        return null;
    }
}
