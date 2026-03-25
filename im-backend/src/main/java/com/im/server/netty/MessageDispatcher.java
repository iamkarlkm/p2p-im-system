package com.im.server.netty;

import com.im.server.netty.dto.WsMessage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息分发器
 */
@Component
public class MessageDispatcher {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);
    
    @Autowired
    private ChatMessageHandler chatMessageHandler;
    
    @Autowired
    private PresenceHandler presenceHandler;
    
    @Autowired
    private AckHandler ackHandler;
    
    /**
     * 消息类型处理器映射
     */
    private final Map<String, MessageHandler> handlers = new ConcurrentHashMap<>();
    
    public MessageDispatcher() {
        // 注册消息处理器
    }
    
    /**
     * 分发消息到对应的处理器
     */
    public void dispatch(WsMessage wsMessage, String userId, Channel channel) {
        String type = wsMessage.getType();
        
        MessageHandler handler = handlers.get(type);
        if (handler != null) {
            handler.handle(wsMessage, userId, channel);
        } else {
            // 默认消息处理
            handleDefault(wsMessage, userId, channel);
        }
    }
    
    /**
     * 默认消息处理
     */
    private void handleDefault(WsMessage wsMessage, String userId, Channel channel) {
        switch (wsMessage.getType()) {
            case "chat":
                chatMessageHandler.handle(wsMessage, userId, channel);
                break;
            case "presence":
                presenceHandler.handle(wsMessage, userId, channel);
                break;
            case "ack":
                ackHandler.handle(wsMessage, userId, channel);
                break;
            default:
                logger.warn("未知消息类型: {}", wsMessage.getType());
        }
    }
    
    /**
     * 注册消息处理器
     */
    public void registerHandler(String type, MessageHandler handler) {
        handlers.put(type, handler);
    }
    
    /**
     * 消息处理器接口
     */
    public interface MessageHandler {
        void handle(WsMessage wsMessage, String userId, Channel channel);
    }
}
