package com.im.local.scheduler.config;

import com.im.local.scheduler.websocket.StaffLocationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket配置
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class SchedulerWebSocketConfig implements WebSocketConfigurer {
    
    private final StaffLocationWebSocketHandler staffLocationHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(staffLocationHandler, "/ws/scheduler/staff/{staffId}")
                .setAllowedOrigins("*");
    }
}
