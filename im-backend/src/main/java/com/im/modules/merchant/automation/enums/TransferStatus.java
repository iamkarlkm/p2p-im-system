package com.im.modules.merchant.automation.enums;

import lombok.Getter;

/**
 * 转人工状态枚举
 */
@Getter
public enum TransferStatus {
    
    QUEUEING(0, "排队中"),
    ASSIGNED(1, "已分配"),
    ACCEPTED(2, "已接入"),
    REJECTED(3, "被拒绝"),
    TIMEOUT(4, "已超时"),
    COMPLETED(5, "已完成");
    
    private final int code;
    private final String desc;
    
    TransferStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public static String getDescByCode(int code) {
        for (TransferStatus status : values()) {
            if (status.code == code) {
                return status.desc;
            }
        }
        return "未知";
    }
}
