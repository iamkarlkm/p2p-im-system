package com.im.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import com.im.backend.websocket.TranslationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * WebSocket翻译配置
 */
@Configuration
@EnableWebSocket
public class TranslationWebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private TranslationWebSocketHandler translationWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(translationWebSocketHandler, "/ws/translation")
                .setAllowedOrigins("*");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        container.setMaxSessionIdleTimeout(600000L); // 10分钟
        return container;
    }
}
