package com.im.service.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * WebSocket 服务启动类
 * 基于 P2P-WS (Netty + p2p-ws-sdk-java)
 */
@SpringBootApplication(scanBasePackages = "com.im.service.websocket")
@EnableScheduling
public class WebSocketServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSocketServiceApplication.class, args);
    }
}
