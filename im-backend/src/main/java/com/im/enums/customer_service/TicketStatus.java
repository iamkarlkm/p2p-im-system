package com.im.enums.customer_service;

import lombok.Getter;

/**
 * 工单状态枚举
 * 功能 #319 - 智能客服与工单管理系统
 */
@Getter
public enum TicketStatus {
    
    PENDING(0, "待处理", "工单已创建，等待分配"),
    PROCESSING(1, "处理中", "客服正在处理"),
    WAITING_CONFIRM(2, "待确认", "等待用户确认"),
    RESOLVED(3, "已解决", "问题已解决"),
    CLOSED(4, "已关闭", "工单已关闭");
    
    private final int code;
    private final String name;
    private final String desc;
    
    TicketStatus(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }
    
    public static TicketStatus fromCode(int code) {
        for (TicketStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
