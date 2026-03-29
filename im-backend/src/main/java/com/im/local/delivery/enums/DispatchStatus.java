package com.im.local.delivery.enums;

import lombok.Getter;

/**
 * 派单状态
 */
@Getter
public enum DispatchStatus {
    
    PENDING("待响应"),
    ACCEPTED("已接受"),
    REJECTED("已拒绝"),
    EXPIRED("已超时");
    
    private final String description;
    
    DispatchStatus(String description) {
        this.description = description;
    }
}
