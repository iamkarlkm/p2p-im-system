package com.im.server.netty;

import com.im.server.netty.dto.WsMessage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 在线状态处理器
 */
@Component
public class PresenceHandler implements MessageDispatcher.MessageHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(PresenceHandler.class);
    
    private static final String ONLINE_USERS_KEY = "im:online:users";
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public void handle(WsMessage wsMessage, String userId, Channel channel) {
        String action = wsMessage.getContent();
        
        switch (action) {
            case "online":
                handleOnline(userId);
                break;
            case "offline":
                handleOffline(userId);
                break;
            default:
                logger.warn("未知的在线状态操作: {}", action);
        }
    }
    
    /**
     * 处理用户上线
     */
    private void handleOnline(String userId) {
        logger.info("用户 {} 上线", userId);
        
        // 存储到Redis
        redisTemplate.opsForSet().add(ONLINE_USERS_KEY, userId);
        
        // 通知好友用户上线
        notifyFriends(userId, "online");
    }
    
    /**
     * 处理用户离线
     */
    private void handleOffline(String userId) {
        logger.info("用户 {} 离线", userId);
        
        // 从Redis移除
        redisTemplate.opsForSet().remove(ONLINE_USERS_KEY, userId);
        
        // 通知好友用户离线
        notifyFriends(userId, "offline");
    }
    
    /**
     * 通知好友用户的在线状态
     */
    private void notifyFriends(String userId, String status) {
        // TODO: 获取用户的好友列表，发送状态通知
        // 这里需要调用好友服务获取好友列表
    }
    
    /**
     * 检查用户是否在线
     */
    public boolean isOnline(String userId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ONLINE_USERS_KEY, userId));
    }
    
    /**
     * 获取所有在线用户
     */
    public Set<Object> getOnlineUsers() {
        return redisTemplate.opsForSet().members(ONLINE_USERS_KEY);
    }
}
