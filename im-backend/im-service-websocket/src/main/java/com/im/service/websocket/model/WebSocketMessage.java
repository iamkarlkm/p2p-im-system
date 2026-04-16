package com.im.service.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * WebSocket 消息模型
 * 
 * 通用消息结构，用于 WebSocket 通信
 * 支持多种消息类型：心跳、聊天、通知、回执等
 * 
 * @author im-modular
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {

    /**
     * 消息类型
     * - ping: 心跳请求
     * - pong: 心跳响应
     * - chat: 单聊消息
     * - group_chat: 群聊消息
     * - read_receipt: 已读回执
     * - recall: 消息撤回
     * - typing: 正在输入
     * - presence: 在线状态
     * - ack: 消息确认
     * - delivery_receipt: 送达回执
     * - system_notification: 系统通知
     * - connected: 连接成功
     * - error: 错误消息
     */
    private String type;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID (单聊时使用)
     */
    private Long receiverId;

    /**
     * 群组ID (群聊时使用)
     */
    private Long groupId;

    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 是否为群组消息
     */
    private Boolean isGroup;

    /**
     * 在线状态 (presence消息使用)
     * - online: 在线
     * - away: 离开
     * - busy: 忙碌
     * - offline: 离线
     */
    private String status;

    /**
     * 消息时间戳
     */
    private Long timestamp;

    /**
     * 消息内容数据
     */
    private Map<String, Object> data;

    // ==================== 便捷工厂方法 ====================

    /**
     * 创建心跳响应消息
     */
    public static WebSocketMessage pong() {
        return WebSocketMessage.builder()
                .type("pong")
                .timestamp(System.currentTimeMillis())
                .data(Map.of("serverTime", System.currentTimeMillis()))
                .build();
    }

    /**
     * 创建连接成功消息
     */
    public static WebSocketMessage connected(Long userId) {
        return WebSocketMessage.builder()
                .type("connected")
                .timestamp(System.currentTimeMillis())
                .data(Map.of(
                        "userId", userId,
                        "serverTime", System.currentTimeMillis()
                ))
                .build();
    }

    /**
     * 创建消息确认
     */
    public static WebSocketMessage ack(String messageId) {
        return WebSocketMessage.builder()
                .type("ack")
                .messageId(messageId)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建错误消息
     */
    public static WebSocketMessage error(String errorCode, String errorMessage) {
        return WebSocketMessage.builder()
                .type("error")
                .timestamp(System.currentTimeMillis())
                .data(Map.of(
                        "code", errorCode,
                        "message", errorMessage
                ))
                .build();
    }
}
