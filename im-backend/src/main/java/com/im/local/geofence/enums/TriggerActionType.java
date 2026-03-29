package com.im.local.geofence.enums;

import lombok.Getter;

/**
 * 触发动作类型
 */
@Getter
public enum TriggerActionType {
    
    SEND_MESSAGE("发送消息"),
    PUSH_NOTIFICATION("推送通知"),
    UPDATE_STATUS("更新状态"),
    CALL_API("调用API");
    
    private final String description;
    
    TriggerActionType(String description) {
        this.description = description;
    }
}
