package com.im.config;

import com.im.websocket.WebSocketConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket配置类
 * 功能 #2: WebSocket实时推送服务
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    @Autowired
    private WebSocketConnectionManager webSocketHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/im")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
