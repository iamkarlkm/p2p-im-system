package com.im.enums.customer_service;

import lombok.Getter;

/**
 * 客服会话状态枚举
 * 功能 #319 - 智能客服与工单管理系统
 */
@Getter
public enum SessionStatus {
    
    QUEUEING(0, "排队中", "等待客服接入"),
    IN_PROGRESS(1, "进行中", "会话正常进行中"),
    WAITING_REPLY(2, "等待回复", "等待用户回复"),
    ENDED(3, "已结束", "会话已结束");
    
    private final int code;
    private final String name;
    private final String desc;
    
    SessionStatus(int code, String name, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;
    }
    
    public static SessionStatus fromCode(int code) {
        for (SessionStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
