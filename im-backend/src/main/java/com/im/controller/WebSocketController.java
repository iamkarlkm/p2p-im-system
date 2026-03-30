package com.im.controller;

import com.im.dto.WsMessageDTO;
import com.im.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket消息控制器 - 长连接服务
 * 功能ID: #3
 * @author developer-agent
 * @since 2026-03-30
 */
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebSocketService webSocketService;

    /**
     * 处理私聊消息
     */
    @MessageMapping("/chat/private/{toUserId}")
    public void handlePrivateMessage(@DestinationVariable String toUserId, 
                                      @Payload WsMessageDTO message) {
        // 设置消息时间戳
        message.setTimestamp(System.currentTimeMillis());
        
        // 发送到目标用户的队列
        messagingTemplate.convertAndSendToUser(
            toUserId, 
            "/queue/private", 
            message
        );
        
        // 发送回执给发送方
        messagingTemplate.convertAndSendToUser(
            message.getFromUserId(),
            "/queue/ack",
            createAckMessage(message.getMessageId(), "delivered")
        );
    }

    /**
     * 处理群聊消息
     */
    @MessageMapping("/chat/group/{groupId}")
    public void handleGroupMessage(@DestinationVariable String groupId,
                                    @Payload WsMessageDTO message) {
        message.setTimestamp(System.currentTimeMillis());
        
        // 广播到群组
        messagingTemplate.convertAndSend(
            "/topic/group/" + groupId,
            message
        );
    }

    /**
     * 处理心跳消息
     */
    @MessageMapping("/heartbeat")
    public void handleHeartbeat(@Payload WsMessageDTO message) {
        // 更新用户在线状态
        webSocketService.updateHeartbeat(message.getFromUserId());
        
        // 发送心跳响应
        messagingTemplate.convertAndSendToUser(
            message.getFromUserId(),
            "/queue/heartbeat",
            createHeartbeatAck()
        );
    }

    /**
     * 处理用户上线通知
     */
    @MessageMapping("/user/online")
    public void handleUserOnline(@Payload WsMessageDTO message) {
        webSocketService.userOnline(message.getFromUserId());
        
        // 广播用户上线状态给好友
        messagingTemplate.convertAndSend(
            "/topic/user/status",
            createStatusMessage(message.getFromUserId(), "online")
        );
    }

    /**
     * 处理用户下线通知
     */
    @MessageMapping("/user/offline")
    public void handleUserOffline(@Payload WsMessageDTO message) {
        webSocketService.userOffline(message.getFromUserId());
        
        // 广播用户下线状态给好友
        messagingTemplate.convertAndSend(
            "/topic/user/status",
            createStatusMessage(message.getFromUserId(), "offline")
        );
    }

    /**
     * 处理消息已读回执
     */
    @MessageMapping("/message/read/{messageId}")
    public void handleReadReceipt(@DestinationVariable String messageId,
                                   @Payload WsMessageDTO message) {
        // 通知发送方消息已读
        messagingTemplate.convertAndSendToUser(
            message.getToUserId(), // 原发送方
            "/queue/read",
            createReadReceipt(messageId, message.getFromUserId())
        );
    }

    // ============== 辅助方法 ==============

    private WsMessageDTO createAckMessage(String messageId, String status) {
        WsMessageDTO ack = new WsMessageDTO();
        ack.setMessageType("ACK");
        ack.setContent(messageId);
        ack.setStatus(status);
        ack.setTimestamp(System.currentTimeMillis());
        return ack;
    }

    private WsMessageDTO createHeartbeatAck() {
        WsMessageDTO ack = new WsMessageDTO();
        ack.setMessageType("HEARTBEAT_ACK");
        ack.setTimestamp(System.currentTimeMillis());
        return ack;
    }

    private WsMessageDTO createStatusMessage(String userId, String status) {
        WsMessageDTO msg = new WsMessageDTO();
        msg.setMessageType("USER_STATUS");
        msg.setFromUserId(userId);
        msg.setContent(status);
        msg.setTimestamp(System.currentTimeMillis());
        return msg;
    }

    private WsMessageDTO createReadReceipt(String messageId, String readerId) {
        WsMessageDTO receipt = new WsMessageDTO();
        receipt.setMessageType("READ_RECEIPT");
        receipt.setMessageId(messageId);
        receipt.setFromUserId(readerId);
        receipt.setTimestamp(System.currentTimeMillis());
        return receipt;
    }
}
