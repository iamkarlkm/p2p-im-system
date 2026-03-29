package com.im.server.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.server.netty.dto.WsMessage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 聊天消息处理器
 */
@Component
public class ChatMessageHandler implements MessageDispatcher.MessageHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageHandler.class);
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ChatService chatService;
    
    @Override
    public void handle(WsMessage wsMessage, String userId, Channel channel) {
        String to = wsMessage.getTo();
        String content = wsMessage.getContent();
        String msgId = wsMessage.getMsgId();
        
        logger.info("收到聊天消息: from={}, to={}, msgId={}", userId, to, msgId);
        
        try {
            // 保存消息到数据库
            chatService.saveMessage(userId, to, content, msgId);
            
            // 发送消息给接收者
            if (to != null && !to.startsWith("group_")) {
                // 单聊
                WebSocketMessageHandler.sendMessage(to, wsMessage);
            } else {
                // 群聊
                chatService.sendToGroup(to, wsMessage);
            }
            
            // 发送已读回执
            sendAck(userId, to, msgId);
            
        } catch (Exception e) {
            logger.error("处理聊天消息失败", e);
        }
    }
    
    /**
     * 发送消息确认
     */
    private void sendAck(String from, String to, String msgId) {
        WsMessage ack = new WsMessage();
        ack.setType("ack");
        ack.setFrom(from);
        ack.setTo(to);
        ack.setMsgId(msgId);
        ack.setTimestamp(System.currentTimeMillis());
        
        // 发送确认给发送者
        WebSocketMessageHandler.sendMessage(from, ack);
    }
}
