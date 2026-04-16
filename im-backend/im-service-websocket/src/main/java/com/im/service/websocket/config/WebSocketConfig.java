package com.im.service.websocket.config;

import com.im.service.websocket.handler.MessageWebSocketHandler;
import com.im.service.websocket.interceptor.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket 配置类
 * 
 * 配置 Spring WebSocket (STOMP协议)
 * 功能特性:
 * 1. STOMP 消息代理配置
 * 2. 心跳检测机制 (30秒间隔)
 * 3. 消息大小限制
 * 4. 连接端点配置
 * 5. 拦截器配置 (认证拦截)
 * 
 * STOMP 目的地规范:
 * - /topic/*    : 广播消息 (群聊、系统通知)
 * - /queue/*    : 点对点消息 (单聊、私信)
 * - /app/*      : 应用消息前缀 (客户端发送)
 * - /user/*     : 用户特定消息前缀
 * 
 * @author im-modular
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    private final MessageWebSocketHandler messageWebSocketHandler;

    // ==================== 配置常量 ====================
    
    /** WebSocket 连接端点 */
    public static final String WS_ENDPOINT = "/ws";
    
    /** SockJS 备选端点 */
    public static final String SOCKJS_ENDPOINT = "/ws/sockjs";
    
    /** 应用消息前缀 */
    public static final String APP_PREFIX = "/app";
    
    /** 用户消息前缀 */
    public static final String USER_PREFIX = "/user";
    
    /** 广播消息前缀 */
    public static final String TOPIC_PREFIX = "/topic";
    
    /** 点对点消息前缀 */
    public static final String QUEUE_PREFIX = "/queue";
    
    /** 心跳发送间隔 (毫秒) */
    public static final long HEARTBEAT_SEND_INTERVAL = 30000L;
    
    /** 心跳接收间隔 (毫秒) */
    public static final long HEARTBEAT_RECEIVE_INTERVAL = 30000L;
    
    /** 消息大小限制 (字节) - 10MB */
    public static final int MESSAGE_SIZE_LIMIT = 10 * 1024 * 1024;
    
    /** 发送缓冲区大小限制 (字节) - 512KB */
    public static final int SEND_BUFFER_SIZE_LIMIT = 512 * 1024;
    
    /** 发送超时 (毫秒) */
    public static final long SEND_TIMEOUT = 10000L;

    // ==================== 消息代理配置 ====================

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 启用简单的内存消息代理
        // /topic: 广播消息 (群聊、系统通知)
        // /queue: 点对点消息 (单聊)
        registry.enableSimpleBroker(TOPIC_PREFIX, QUEUE_PREFIX)
                // 设置心跳间隔: 服务端发送间隔, 服务端接收间隔
                .setHeartbeatValue(new long[]{HEARTBEAT_SEND_INTERVAL, HEARTBEAT_RECEIVE_INTERVAL})
                // 设置任务调度器
                .setTaskScheduler(heartbeatTaskScheduler());
        
        // 设置应用消息前缀
        // 客户端发送消息的目的地必须以 /app 开头
        registry.setApplicationDestinationPrefixes(APP_PREFIX);
        
        // 设置用户消息前缀
        // 用于点对点消息发送，格式: /user/{userId}/queue/messages
        registry.setUserDestinationPrefix(USER_PREFIX);
        
        log.info("WebSocket 消息代理配置完成: topics={}, queue={}, appPrefix={}", 
                TOPIC_PREFIX, QUEUE_PREFIX, APP_PREFIX);
    }

    // ==================== 端点配置 ====================

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 标准 WebSocket 端点
        registry.addEndpoint(WS_ENDPOINT)
                // 允许跨域 (生产环境应配置具体域名)
                .setAllowedOriginPatterns("*")
                // 添加自定义处理器
                .addInterceptors(webSocketAuthInterceptor)
                // 设置处理器
                .setHandshakeHandler(messageWebSocketHandler.getHandshakeHandler());
        
        // SockJS 备选端点 (兼容旧浏览器)
        registry.addEndpoint(SOCKJS_ENDPOINT)
                .setAllowedOriginPatterns("*")
                .addInterceptors(webSocketAuthInterceptor)
                .setHandshakeHandler(messageWebSocketHandler.getHandshakeHandler())
                // 启用 SockJS 支持
                .withSockJS()
                // 设置 SockJS 选项
                .setStreamBytesLimit(SEND_BUFFER_SIZE_LIMIT)
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(5000L);
        
        log.info("WebSocket 端点注册完成: endpoint={}, sockjsEndpoint={}", 
                WS_ENDPOINT, SOCKJS_ENDPOINT);
    }

    // ==================== 客户端入站通道配置 ====================

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 配置线程池
        registration.taskExecutor()
                // 核心线程数
                .corePoolSize(4)
                // 最大线程数
                .maxPoolSize(10)
                // 队列容量
                .queueCapacity(1000)
                // 线程名称前缀
                .threadNamePrefix("ws-inbound-");
        
        // 添加拦截器链
        // 认证拦截器必须在最前面，确保所有消息都经过认证
        registration.interceptors(webSocketAuthInterceptor);
        
        log.info("WebSocket 入站通道配置完成: corePoolSize=4, maxPoolSize=10");
    }

    // ==================== 客户端出站通道配置 ====================

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(4)
                .maxPoolSize(10)
                .queueCapacity(1000)
                .threadNamePrefix("ws-outbound-");
        
        log.info("WebSocket 出站通道配置完成: corePoolSize=4, maxPoolSize=10");
    }

    // ==================== WebSocket 传输配置 ====================

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        // 设置消息大小限制
        registration.setMessageSizeLimit(MESSAGE_SIZE_LIMIT);
        
        // 设置发送缓冲区大小限制
        registration.setSendBufferSizeLimit(SEND_BUFFER_SIZE_LIMIT);
        
        // 设置发送超时
        registration.setSendTimeLimit((int) SEND_TIMEOUT);
        
        log.info("WebSocket 传输配置完成: messageSizeLimit={}, bufferSizeLimit={}, sendTimeout={}",
                MESSAGE_SIZE_LIMIT, SEND_BUFFER_SIZE_LIMIT, SEND_TIMEOUT);
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建心跳任务调度器
     */
    private TaskScheduler heartbeatTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.setDaemon(true);
        scheduler.initialize();
        return scheduler;
    }
}
