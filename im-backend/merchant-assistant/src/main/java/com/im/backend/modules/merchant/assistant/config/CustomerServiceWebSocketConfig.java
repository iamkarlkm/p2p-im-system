package com.im.backend.modules.merchant.assistant.config;

import com.im.backend.modules.merchant.assistant.websocket.CustomerServiceWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

/**
 * 客服WebSocket配置
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class CustomerServiceWebSocketConfig implements WebSocketConfigurer {
    
    private final CustomerServiceWebSocketHandler webSocketHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/assistant/chat")
                .setAllowedOrigins("*");
    }
}
