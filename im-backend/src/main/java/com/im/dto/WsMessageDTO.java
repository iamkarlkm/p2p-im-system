package com.im.dto;

import lombok.Data;

/**
 * WebSocket消息传输对象
 * 功能ID: #3
 */
@Data
public class WsMessageDTO {
    private String messageId;
    private String messageType; // TEXT, IMAGE, FILE, ACK, HEARTBEAT, USER_STATUS, READ_RECEIPT
    private String fromUserId;
    private String toUserId;
    private String groupId;
    private String content;
    private String status;
    private long timestamp;
    private Object extra; // 扩展字段
}
