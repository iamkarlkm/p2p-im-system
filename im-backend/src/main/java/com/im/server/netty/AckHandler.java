package com.im.server.netty;

import com.im.server.netty.dto.WsMessage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 消息确认处理器
 */
@Component
public class AckHandler implements MessageDispatcher.MessageHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(AckHandler.class);
    
    @Override
    public void handle(WsMessage wsMessage, String userId, Channel channel) {
        String msgId = wsMessage.getMsgId();
        
        logger.debug("收到消息确认: userId={}, msgId={}", userId, msgId);
        
        // TODO: 更新消息状态为已送达
        // 1. 更新Redis中的消息状态
        // 2. 如果接收者已读，标记为已读
    }
}
